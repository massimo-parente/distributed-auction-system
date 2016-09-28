package actors

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, Cancellable, LoggingFSM, Terminated}
import akka.pattern.pipe

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import models._
import AuctionControllerActor._


object AuctionControllerActor {

  sealed trait AuctionState

  case object Closed extends AuctionState

  case object AwaitingCall extends AuctionState

  case object AwaitingBidders extends AuctionState

  case object InProgress extends AuctionState

  case object Complete extends AuctionState

  case class ResolveUserNames(sender: ActorRef, users: Seq[String])

  case class ResolveBudget(sender: ActorRef, budget: Int)

  // internal messages
  case class Subscribe(name: String)

  case class UnSubscribe(name: String)

  case object InitAuction

  case object AbortAuction

  case class Tick(sender: ActorRef)

  case object GetStatus

  object MessageType {
    val CALL_AUCTION = "call-auction"
    val JOIN_AUCTION = "join-auction"
    val BID = "bid"
    val CHAT = "chat"
    val AUCTION_INITIALISED = "auction-initialised"
    val AUCTION_REQUESTED = "auction-requested"
    val AUCTION_JOINED = "auction-joined"
    val AUCTION_OPENED = "auction-opened"
    val BID_ACCEPTED = "bid-accepted"
    val BID_REJECTED = "bid-rejected"
    val AUCTION_TICKED = "auction-ticked"
    val AUCTION_COMPLETED = "auction-completed"
    val SUBSCRIBED = "subscribed"
    val UNSUBSCRIBED = "unsubscribed"
    val COMMAND_REJECTED = "command-rejected"
  }

  import MessageType._

  sealed trait AuctionMessage
  // inbound
  case class CallAuction(auctioneer: String, player: String, messageType: String = CALL_AUCTION) extends AuctionMessage
  case class JoinAuction(bidder: String, player: String, messageType: String = JOIN_AUCTION) extends AuctionMessage
  case class Bid(bidder: String, player: String, value: Int, messageType: String = BID) extends AuctionMessage
  // both inbound and outbound
  case class Chat(sender: String, message: String, messageType: String = CHAT) extends AuctionMessage
  // outbound
  case class AuctionInitialised(auctioneer: String, messageType: String = AUCTION_INITIALISED) extends AuctionMessage
  case class AuctionRequested(auctioneer: String, player: String, pendingBidders: Seq[String], messageType: String = AUCTION_REQUESTED) extends AuctionMessage
  case class AuctionJoined(bidder: String, pendingBidders: Seq[String], messageType: String = AUCTION_JOINED) extends AuctionMessage
  case class AuctionOpened(player: String, messageType: String = AUCTION_OPENED) extends AuctionMessage
  case class BidAccepted(bid: Bid, messageType: String = BID_ACCEPTED) extends AuctionMessage
  case class BidRejected(bid: Bid, reason: String, messageType: String = BID_REJECTED) extends AuctionMessage
  case class Ticked(bid: Bid, count: Int, messageType: String = AUCTION_TICKED) extends AuctionMessage
  case class AuctionCompleted(bid: Bid, messageType: String = AUCTION_COMPLETED) extends AuctionMessage
  case class Subscribed(user: String, messageType: String = SUBSCRIBED) extends AuctionMessage
  case class UnSubscribed(user: String, messageType: String = UNSUBSCRIBED) extends AuctionMessage
  case class CommandRejected(command: AuctionMessage, reason: String, messageType: String = COMMAND_REJECTED)
  case class AuctionData(auctioneers: Seq[String] = Seq.empty,
                         pendingBidders: Seq[String] = Seq.empty,
                         currentBid: Bid = null,
                         scheduler: Cancellable = null,
                         auctionCount: Int = 1)

  implicit val bidReads: Reads[Bid] = (
    (__ \ "bidder").read[String] and
      (__ \ "player").read[String] and
      (__ \ "value").read[String].map(_.toInt) and
      (__ \ "messageType").read[String]
    ) (Bid)
  implicit val bidWrites = Json.writes[Bid]
  implicit val bidFormat = Format(bidReads, bidWrites)

  implicit val callAuctionFormat = Json.format[CallAuction]
  implicit val joinAuctionFormat = Json.format[JoinAuction]
  implicit val chatFormat = Json.format[Chat]
  implicit val auctionInitialisedFormat = Json.format[AuctionInitialised]
  implicit val auctionRequestedFormat = Json.format[AuctionRequested]
  implicit val auctionJoinedFormat = Json.format[AuctionJoined]
  implicit val auctionOpenedFormat = Json.format[AuctionOpened]
  implicit val bidAcceptedFormat = Json.format[BidAccepted]
  implicit val bidRejectedFormat = Json.format[BidRejected]
  implicit val auctionCompletedFormat = Json.format[AuctionCompleted]
  implicit val tickedFormat = Json.format[Ticked]
  implicit val subscribedFormat = Json.format[Subscribed]
  implicit val unSubscribedFormat = Json.format[UnSubscribed]

  implicit class AuctionMessageToJson(m: AuctionMessage) {
    def toJson(): JsValue = m match {
      case m: Chat => Json.toJson(m)
      case m: AuctionInitialised => Json.toJson(m)
      case m: AuctionRequested => Json.toJson(m)
      case m: AuctionJoined => Json.toJson(m)
      case m: AuctionOpened => Json.toJson(m)
      case m: BidAccepted => Json.toJson(m)
      case m: BidRejected => Json.toJson(m)
      case m: Ticked => Json.toJson(m)
      case m: AuctionCompleted => Json.toJson(m)
      case m: Subscribed => Json.toJson(m)
      case m: UnSubscribed => Json.toJson(m)
      //case m: CommandRejected => Json.toJson(m)
    }
  }

}

/**
  * Created by mparente on 19/09/2016.
  */
class AuctionControllerActor @Inject()(userRepo: UserRepository,
                                       playerRepo: PlayerRepository,
                                       eventRepo: EventRepository)
                                      (implicit ec: ExecutionContext)
  extends Actor with LoggingFSM[AuctionState, AuctionData] {

  var subscribers: Map[ActorRef, String] = Map.empty

  startWith(Closed, AuctionData())

  when(Closed) {
    case Event(InitAuction, data) =>
      getUserNames(sender()) pipeTo self
      stay()
    case Event(ResolveUserNames(sender, users), data) =>
      val auctioneers = users.filter(_ => true)
      val newData = data.copy(auctioneers = auctioneers)
      broadcast(AuctionInitialised(users(0)))
      goto(AwaitingCall) using (newData)
  }

  when(AwaitingCall) {
    case Event(CallAuction(auctioneer, player, _), data) =>
      log.info("{} has called an auction on {}", auctioneer, player)
      val bid = Bid(auctioneer, player, 1)
      val pendingBidders = data.auctioneers.filter(a => !a.equals(auctioneer))
      val newData = data.copy(pendingBidders = pendingBidders, currentBid = bid)
      broadcast(AuctionRequested(auctioneer, player, pendingBidders))
      goto(AwaitingBidders) using (newData)
  }

  when(AwaitingBidders) {
    case Event(JoinAuction(bidder, player, _), data) =>
      log.info("{} is joining the auction", bidder)
      val bidders = data.pendingBidders.filterNot(_.equals(bidder))
      if (bidders.isEmpty) {
        log.info("All bidders have joined. GAME ON!!!")
        broadcast(AuctionOpened(player))
        val scheduler = startCounter()
        val newData = data.copy(pendingBidders = bidders, scheduler = scheduler)
        goto(InProgress) using (newData)
      } else {
        log.info("More bidders to join...")
        broadcast(AuctionJoined(bidder, bidders))
        val newData = data.copy(pendingBidders = bidders)
        stay() using (newData)
      }
  }

  when(InProgress) {
    case Event(bid@Bid(bidder, player, value, _), data) =>
      log.info("New bid received: {}", bid)
      validateBid(bid: Bid)(data: AuctionData) match {
        case Right(bidAccepted) =>
          data.scheduler.cancel()
          val scheduler = startCounter()
          val newData = data.copy(scheduler = scheduler, currentBid = bid, auctionCount = 1)
          broadcast(bidAccepted)
          stay() using (newData)
        case Left(bidRejected) =>
          broadcast(bidRejected)
          stay() using (data)
      }
    case Event(Tick(sender), data) =>
      log.info("Tick {}", data.auctionCount)
      if (data.auctionCount == 3) {
        data.scheduler.cancel()
        log.info("Auction complete!!! Wins highest bid: {}", data.currentBid)
        // complete auction
        broadcast(Ticked(data.currentBid, data.auctionCount))
        broadcast(AuctionCompleted(data.currentBid))
        signPlayer(data.currentBid)
        // prepare new auction
        val auctioneers = shiftLeft(data.auctioneers)
        val newData = new AuctionData(auctioneers = auctioneers)
        broadcast(AuctionInitialised(auctioneers(0)))
        goto(AwaitingCall) using (newData)
      } else {
        broadcast(Ticked(data.currentBid, data.auctionCount))
        val newData = data.copy(auctionCount = data.auctionCount + 1)
        stay() using (newData)
      }
  }

  whenUnhandled {
    case Event(Subscribe(user), _) =>
      log.info("Subscribed {}", user)
      subscribers += (sender -> user)
      context.watch(sender())
      broadcast(Subscribed(user))
      stay()
    case Event(Terminated(actor), _) =>
      log.info("Unsubscribed {}", subscribers(actor))
      broadcast(UnSubscribed(subscribers(actor)))
      subscribers -= actor
      stay()
    case Event(AbortAuction, data) =>
      goto(Closed) using (AuctionData(data.auctioneers))
    case Event(c@Chat(sender, message, _), _) =>
      broadcast(c)
      stay()
    case Event(message: AuctionMessage, _) =>
      val error = s"Message $message cannot be handel in state: $stateName"
      log.warning(error)
      stay()
  }

  def persistEvent(msg: AuctionMessage, savePoint: Boolean) = {
    val e = models.Event(None, Json.stringify(msg.toJson()), savePoint)
    eventRepo.add(e)
  }

  def broadcast(msg: AuctionMessage) = {
    msg match {
      case m: AuctionInitialised => persistEvent(m, true)
      case m: Subscribe => // don't persist
      case m: UnSubscribe => // don't persist
      case other => persistEvent(other, false)
    }
    subscribers.keySet.foreach(_ ! msg)
  }

  // make async
  def validateBid(bid: Bid)(data: AuctionData): Either[BidRejected, BidAccepted] = {
    if (!bid.player.equals(data.currentBid.player)) {
      return Left(BidRejected(bid, s"Bid for ${bid.player} not compatible with current auction for ${data.currentBid.player}"))
    }
    if (bid.value <= data.currentBid.value) {
      return Left(BidRejected(bid, s"Bid value must be higher than current value of ${data.currentBid.value}"))
    }
    val budget = resolveFuture(
      userRepo.getBudget(bid.bidder)
    )
    if (bid.value > budget) {
      return Left(BidRejected(bid, s"Bid amount of ${bid.value} exceeds budget of ${budget}"))
    }
    userRepo.updateBudget(bid.bidder, (budget - bid.value))
    Right(BidAccepted(bid))
  }

  def validateBudget(bid: Bid, budget: Int): Either[BidRejected, BidAccepted] = {
    if (bid.value > budget) {
      Left(BidRejected(bid, s"Bid amount ${bid.value} exceeds budget $budget"))
    } else {
      Right(BidAccepted(bid))
    }
  }

  def startCounter(): Cancellable = {
    context.system.scheduler.schedule(3 seconds, 3 seconds, self, Tick(sender()))
  }

  def getUserNames(sender: ActorRef): Future[ResolveUserNames] = {
    userRepo.getUserNames().map { names =>
      log.info(names.toString())
      ResolveUserNames(sender, names)
    }
  }

  def signPlayer(bid: Bid): Unit = {
    playerRepo.signPlayer(bid.player, bid.bidder, bid.value)
  }

  def shiftLeft[A](seq: Seq[A]): Seq[A] = {
    // no need to check input as seq is always populated in auction
    seq.tail :+ seq.head
  }

  def resolveFuture[A](f: Future[A]): A = {
    Await.result(f, 1 minute)
  }

  initialize()
}