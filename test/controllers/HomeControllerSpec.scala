package controllers

import java.io.File

import org.scalatest._
import org.scalatestplus.play._
import play.api.test.Helpers.{GET => GET_REQUEST, _}
import play.api.libs.ws._
import play.api.mvc._
import Results._
import actors.WebSocketActor
import com.sun.xml.internal.bind.v2.TODO
import org.asynchttpclient.AsyncHttpClient
import org.scalatest.concurrent.ScalaFutures
import play.api.{Application, Environment, Mode}
import play.api.inject.guice._
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.routing._
import play.api.routing.sird._
import play.api.test.{Helpers, TestServer}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by mparente on 23/09/2016.
  */
class HomeControllerSpec extends PlaySpec with ScalaFutures with OneAppPerTest {

  //override def newAppForTest(testData: TestData) = new GuiceApplicationBuilder().in(Environment.simple())build()

  "HomeController" must {

    "send/receive to/from web-socket channel" in {

      Helpers.running(TestServer(9999, app)) {
        //val url = s"ws://localhost:$port/ws"
        val url = s"http://localhost:9999/test"

        val wsClient = app.injector.instanceOf[WSClient]
        val f = wsClient.url(url).get()
        val response = Await.result(f, Duration.Inf)
        println("************" + response)
      }
      //val webSocketClient = new WebSocketClient(wsClient.underlying[AsyncHttpClient])
      //val listener = new WebSocketClient.LoggingListener
      //val completionStage = webSocketClient.call(url, "ws://localhost", listener)
      //val response = Await.result(wsClient.url(url).get(), Duration.Inf)
      //response.status mustBe (200)

      //val asyncHttpClient: AsyncHttpClient = client.underlying[AsyncHttpClient]
      //val webSocketClient = new WebSocketClient(wsClient.underlying[AsyncHttpClient])
      //val listener = new WebSocketClient.LoggingListener
      //val completionStage = webSocketClient.call(url, "ws://localhost", listener)
      //val f = FutureConverters.toScala(completionStage)
      //        whenReady(f, timeout = Timeout(1 second)) { webSocket =>
      //          println("heerererere " + webSocket)
      //          //webSocket mustBe a[WebSocket]
      //        }
      //      }
    }
    //      Helpers.running(TestServer(port, app)) {
    //        val url = s"localhost:$port"
    //        val serverURL = s"ws://$url/ws"
    //
    //        val asyncHttpClient: AsyncHttpClient = client.underlying[AsyncHttpClient]
    //
    //        val webSocketClient = new WebSocketClient(asyncHttpClient)
    //        try {
    //          val origin = "ws://example.com/ws"
    //          val listener = new WebSocketClient.LoggingListener
    //          val completionStage = webSocketClient.call(serverURL, origin, listener)
    //          val f = FutureConverters.toScala(completionStage)
    //          val result = Await.result(f, atMost = 1000 millis)
    //          listener.getThrowable mustBe a[IllegalStateException]
    //        } catch {
    //          case e: java.util.concurrent.ExecutionException =>
    //            val foo = e.getCause
    //            foo mustBe an [IllegalStateException]
    //          //case e: IllegalStateException =>
    //          //  e mustBe an [IllegalStateException]
    //        }
  }
}
