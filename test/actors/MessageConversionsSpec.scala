package actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestFSMRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers, WordSpecLike}
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.Json

import scala.concurrent.duration._

/**
  * Created by mparente on 19/09/2016.
  */
class MessageConversionsSpec extends FlatSpec
  with Matchers
  with BeforeAndAfterAll
  with OneAppPerSuite {

  import AuctionControllerActor._


  "A message" must "convert to/from json" in {

    val call = CallAuction("user1", "player1")
    val join = JoinAuction("user1", "player1")
    val bid = Bid("user1", "player1", 10)
    val chat = Chat("user1", "abc")

//    assert(call.equals(Json.toJson(call).toAuctionMessage))
//    assert(call.equals(Json.toJson(call).as[AuctionMessage]))
//    assert(join.equals(Json.toJson(join).as[AuctionMessage]))
//    assert(bid.equals(Json.toJson(bid).as[AuctionMessage]))
//    assert(chat.equals(Json.toJson(chat).as[AuctionMessage]))
  }


}
