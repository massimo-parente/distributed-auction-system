package actors

import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.Json

/**
  * Created by mparente on 19/09/2016.
  */
class ProtocolSpec extends FlatSpec
  with Matchers
  with BeforeAndAfterAll
  with OneAppPerSuite {


  "A message converter" must "convert to/from json" in {

    val call = CallAuction("user1", "player1")
    val join = JoinAuction("user1", "player1")
    val bid = Bid("user1", "player1", 10)
    val chat = Chat("user1", "abc")

    assert(call.equals(Json.toJson(call).as[AuctionMessage]))
    assert(join.equals(Json.toJson(join).as[AuctionMessage]))
    assert(bid.equals(Json.toJson(bid).as[AuctionMessage]))
    assert(chat.equals(Json.toJson(chat).as[AuctionMessage]))
  }


}
