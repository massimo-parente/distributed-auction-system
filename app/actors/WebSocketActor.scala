package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.event.LoggingReceive
import akka.actor.ActorRef
import akka.actor.Props
import play.api.libs.json._
import play.api.libs.functional.syntax._
import AuctionControllerActor._

object WebSocketActor {

  def props(uid: String, auctionControllerActor: ActorRef)(out: ActorRef) = {
    Props(new WebSocketActor(uid, auctionControllerActor, out))
  }

  implicit val bidReads: Reads[Bid] = (
    (__ \ "bidder").read[String] and
    (__ \ "player").read[String] and
    (__ \ "value").read[String].map(_.toInt) and
    (__ \ "messageType").read[String]
  )(Bid)
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

}
class WebSocketActor(username: String,
                     auctionControllerActor: ActorRef,
                     out: ActorRef) extends Actor with ActorLogging {

  import WebSocketActor._
  import AuctionControllerActor.MessageType._

  override def preStart() = {
    auctionControllerActor ! Subscribe(username)
  }

  override def postStop(): Unit = {
    println("stopped here")
  }

  def receive = LoggingReceive {

    case m: AuctionMessage if sender == auctionControllerActor =>
      log.info("Processing outbound message {}", m)
      val js = m match {
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
      out ! js

    case js: JsValue =>
      log.info("Processing inbound message {}", js)
      val msg = (js \ "messageType").as[String] match {
        case CALL_AUCTION=> js.as[CallAuction]
        case JOIN_AUCTION => js.as[JoinAuction]
        case BID => js.as[Bid]
        case CHAT => js.as[Chat]
      }
      auctionControllerActor ! msg

    case other =>
      log.error("unhandled: " + other)
  }
}
