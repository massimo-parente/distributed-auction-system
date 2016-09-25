package actors

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill}
import akka.testkit.{ImplicitSender, TestActorRef, TestFSMRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.scalatestplus.play.OneAppPerSuite

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by mparente on 19/09/2016.
  */
class AuctionControllerActorSpec extends TestKit(ActorSystem("TestSystem"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with OneAppPerSuite {

  import models._
  import AuctionControllerActor._
  import testhelpers.{DatabaseFixturesConfig, Injector}

  val dbConfig = Injector.inject[DatabaseFixturesConfig]
  val userRepo = Injector.inject[UserRepositoryImpl]
  val playerRepo = Injector.inject[PlayerRepositoryImpl]

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An AuctionControllerActor" must {

    "can cycle auctioneers" in {
      val ref = TestFSMRef(new AuctionControllerActor(userRepo, playerRepo))
      val shift: Seq[Int] => Seq[Int] = ref.underlyingActor.shiftLeft

      val s = Seq(1, 2, 3)
      val s1 = shift(shift(shift(s)))
      assert(s == s1)
    }

    "subscribe/unsbscribe users" in {
      class Subscriber(id: String, actor: ActorRef) extends Actor {
        override def preStart(): Unit = {
          actor ! Subscribe(id)
        }
        override def receive: Receive = {
          case _ => //
        }
      }

      val actor = TestFSMRef(new AuctionControllerActor(userRepo, playerRepo))
      actor ! Subscribe("me")
      expectMsg(Subscribed("me"))
      assert(actor.underlyingActor.subscribers.size == 1)

      val sub1 = TestActorRef[Subscriber](new Subscriber("user1", actor))
      expectMsg(Subscribed("user1"))
      assert(actor.underlyingActor.subscribers.size == 2)

      val sub2 = TestActorRef[Subscriber](new Subscriber("user2", actor))
      expectMsg(Subscribed("user2"))
      assert(actor.underlyingActor.subscribers.size == 3)

      sub1 ! PoisonPill
      expectMsg(UnSubscribed("user1"))
      assert(actor.underlyingActor.subscribers.size == 2)

      sub2 ! PoisonPill
      expectMsg(UnSubscribed("user2"))
      assert(actor.underlyingActor.subscribers.size == 1)

    }

    "run full auction" in {

      val actor = TestFSMRef(new AuctionControllerActor(userRepo, playerRepo))

      actor ! Subscribe("me")
      expectMsg(Subscribed("me"))

      // initial status
      assert(actor.stateName == Closed)

      actor ! InitAuction
      expectMsg(AuctionInitialised("user1"))
      assert(actor.stateName == AwaitingCall)
      assert(actor.stateData.auctioneers == Seq("user1", "user2", "user3"))

      actor ! CallAuction("user1", "player1")
      expectMsg(AuctionRequested("user1", "player1", Seq("user1", "user2", "user3")))
      assert(actor.stateName == AwaitingBidders)
      assert(actor.stateData.pendingBidders.size == 3)

      // join first user
      actor ! JoinAuction("user1", "player1")
      assert(actor.stateName == AwaitingBidders)
      assert(actor.stateData.pendingBidders.size == 2)

      // join first user
      actor ! JoinAuction("user2", "player1")
      assert(actor.stateName == AwaitingBidders)
      assert(actor.stateData.pendingBidders.size == 1)

      // join third user and start auction
      actor ! JoinAuction("user3", "player1")
      expectMsg(AuctionOpened("player1"))
      assert(actor.stateName == InProgress)
      assert(actor.stateData.pendingBidders.isEmpty)

      expectMsg(1 minute, Ticked(Bid("user1", "player1", 1), 1))
      expectMsg(1 minute, Ticked(Bid("user1", "player1", 1), 2))

      actor ! Bid("user3", "player1", 10)
      expectMsg(1 minute, BidAccepted(Bid("user3", "player1", 10)))

      expectMsg(1 minute, Ticked(Bid("user3", "player1", 10), 1))
      expectMsg(1 minute, Ticked(Bid("user3", "player1", 10), 2))
      expectMsg(1 minute, Ticked(Bid("user3", "player1", 10), 3))

      expectMsg(1 minute, AuctionCompleted(Bid("user3", "player1", 10)))

      expectMsg(1 minute, AuctionInitialised("user2"))

      awaitCond(actor.stateName == AwaitingCall)
      assert(actor.stateData.auctioneers == Seq("user2", "user3", "user1"))
    }

    "reject invalid state transitions" in {

      val actor = TestFSMRef(new AuctionControllerActor(userRepo, playerRepo))

      // initial status
      assert(actor.stateName == Closed)

      // first message should be Init
//      actor ! CallAuction("user1", "player1")
//      expectMsgType[MessageRejected]
//      assert(actor.stateName == Closed)
    }
  }

}
