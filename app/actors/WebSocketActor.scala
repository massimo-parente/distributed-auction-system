package actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.event.LoggingReceive
import akka.actor.ActorRef
import akka.actor.Props
import play.api.libs.json._
import AuctionControllerActor._
import AuctionControllerActor.MessageType._

object WebSocketActor {

  def props(uid: String, auctionControllerActor: ActorRef)(out: ActorRef) = {
    Props(new WebSocketActor(uid, auctionControllerActor, out))
  }

}
class WebSocketActor(username: String,
                     auctionControllerActor: ActorRef,
                     out: ActorRef) extends Actor with ActorLogging {

  override def preStart() = {
    auctionControllerActor ! Subscribe(username)
  }

  override def postStop(): Unit = {
    println("stopped here")
  }

  def receive = LoggingReceive {

    case m: AuctionMessage if sender == auctionControllerActor =>
      log.info("Processing outbound message {}", m)
      out ! m.toJson()

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
