package controllers

import javax.inject.{Inject, Named}

import actors.AuctionControllerActor.{AbortAuction, Closed, GetStatus, InitAuction}
import actors.WebSocketActor
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import models._
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.io.Source

class HomeController @Inject()(@Named("auctionControllerActor") auctionControllerActor: ActorRef,
                               userRepo: UserRepository,
                               playerRepo: PlayerRepository,
                               eventRepo: EventRepository)
                              (implicit system: ActorSystem, materializer: Materializer, ec: ExecutionContext)
  extends Controller {

  private val logger = org.slf4j.LoggerFactory.getLogger("controllers.Application")

  import models.Users._
  import models.Players._

  def ws = WebSocket.acceptOrResult[JsValue, JsValue] { implicit request =>
    logger.info("Connecting websocket...")
    Future.successful(request.session.get("username") match {
      case None =>
        logger.info("No user in session")
        Left(Unauthorized("Oops, you are not authorized"))
      case Some(username) =>
        logger.info("Found user in session")
        Right(ActorFlow.actorRef(WebSocketActor.props(username, auctionControllerActor)))
    })
  }

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def login = Action.async(BodyParsers.parse.json) { implicit request =>
    (request.body \ "username").validate[String] fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      username => {
        userRepo.getUser(username).map {
          case Some(user) =>
            val js = Json.toJson(user)
            logger.info(username + " logged in")
            Ok(js).withSession(request.session + ("username" -> username))
          case None =>
            Unauthorized(Json.obj("message" -> Json.toJson("Unknown username" + username)))
        }
      })
  }

  def logout = Action { implicit request =>
    Ok(Json.toJson("You have been logged out")).withNewSession
  }

  def startAuction = Action {
    logger.info("Starting auction")
    auctionControllerActor ! InitAuction
    Ok(Json.obj("success" -> "Auction started"))
  }

  def abortAuction = Action {
    logger.info("Aborting auction")
    auctionControllerActor ! AbortAuction
    Ok(Json.obj("success" -> "Auction aborted"))
  }

  def events = Action.async {
    implicit val timeout = Timeout(10 seconds)
    (auctionControllerActor ? GetStatus)
      .flatMap {
        case Closed =>
          Future.successful(Ok(Json.toJson("")))
        case _ =>
          eventRepo.findFromLatestSavePoint()
            .map(_.map(e => Json.toJson(e.payload)))
            .map(events => Ok(Json.toJson(events)))
      }
  }

  def addUser() = Action.async(BodyParsers.parse.json) { implicit request =>
    request.body.validate[User].fold(
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      user => {
        userRepo.addUser(user)
          .map(_ => Ok(s"User ${user.name} created"))
      }
    )
  }

  def updateUser(name: String, role: String, budget: Int) = Action.async {
    userRepo.updateUser(name, role, budget).map(_ => Ok(""))
  }

  def deleteUser(name: String) = Action.async {
    userRepo.deleteUser(name).map(_ => Ok(s"User $name deleted"))
  }

  def getUsers() = Action.async {
    userRepo.findAll().map(users => Ok(Json.toJson(users)))
  }

  def getUser(name: String) = Action.async {
    userRepo.getUser(name).map(user => Ok(Json.toJson(user)))
  }

  def getTeams() = Action.async {
    userRepo.findAll().map(teams => Ok(Json.toJson(teams)))
  }

  def getPlayers(user: String) = Action.async {
    playerRepo.findPlayersOfUser(user).map(players => Ok(Json.toJson(players)))
  }

  def getAllPlayers() = Action.async {
    playerRepo.findAll().map(players => Ok(Json.toJson(players)))
  }

  def loadPlayers = Action.async { implicit request =>
    parseCSV(request.body.asText.get)
    Future.successful(Ok(""))
  }

  def parseCSV(payload: String) {
    val bufferedSource = Source.fromString(payload)
    for (line <- bufferedSource.getLines) {
      val cols = line.split(",").map(_.trim)
      playerRepo.addPlayer(new Player(cols(0), cols(1), 0, cols(2), None))
    }
    bufferedSource.close
  }

}
