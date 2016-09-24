package actors

import actors.AuctionControllerActor.{Bid, BidAccepted, Chat}
import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill}
import akka.testkit.{ImplicitSender, TestActorRef, TestFSMRef, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.Json

import scala.concurrent.duration._

/**
  * Created by mparente on 19/09/2016.
  */
class WebSocketActorSpec extends TestKit(ActorSystem("TestSystem"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with OneAppPerSuite {

  import models._
  import testhelpers.{DatabaseFixturesConfig, Injector}
  import WebSocketActor._

  val dbConfig = Injector.inject[DatabaseFixturesConfig]
  val userRepo = Injector.inject[UserRepositoryImpl]
  val playerRepo = Injector.inject[PlayerRepositoryImpl]

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A WebSocketActor" must {
    "mediate between web-socket and auction controller" in {

      val out = TestProbe()
      val auctionControllerActor = TestActorRef[FakeAuctionControllerActor]
      val ref = TestActorRef[WebSocketActor](new WebSocketActor("user1", auctionControllerActor, out.ref))

      val msg = Json.toJson(Chat("me", "hello"))
      ref ! msg
      out.expectMsg(msg)
    }
  }
}

class FakeAuctionControllerActor extends Actor {
  override def receive: Receive = {
    case m: Chat =>
      sender ! m
    case m: Bid =>
      sender ! BidAccepted(m)
  }
}
