package controllers

import javax.inject.{Inject, Named}

import actors.AuctionControllerActor.{AbortAuction, AuctionMessage, AuctionState, Closed, GetStatus, InitAuction}
import actors.WebSocketActor
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import config.DatabaseConfig
import models.{EventRepository, Player, PlayerRepository, User}
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import slick.driver.H2Driver.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.io.Source

class HomeController @Inject()(@Named("auctionControllerActor") auctionControllerActor: ActorRef,
                               dbConfig: DatabaseConfig,
                               playerRepo: PlayerRepository,
                               eventRepo: EventRepository)
                              (implicit system: ActorSystem, materializer: Materializer)
  extends Controller { //with CouchbaseController {

  private val logger = org.slf4j.LoggerFactory.getLogger("controllers.Application")

//  implicit val couchbaseExecutionContext = PlayCouchbase.couchbaseExecutor

  import dbConfig._
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
        db.run(
          users.filter(_.name === username).take(1).result
        ).map(_.headOption).map {
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
        db.run(users += user)
          .map(_ => Ok(s"User ${user.name} created"))
      }
    )
  }

  def updateUser(name: String, role: String, budget: Int) = Action.async {
    db.run(
      users.filter(_.name === name)
        .map(user => (user.budget, user.role))
        .update(budget, role)
    ).map(_ => Ok(""))
  }

  def deleteUser(name: String) = Action.async {
    db.run(users.filter(_.name === name).delete)
      .map(_ => Ok(s"User $name deleted"))
  }

  def getUsers() = Action.async {
    db.run(users.result).map(users => Ok(Json.toJson(users)))
  }

  def getUser(name: String) = Action.async {
    db.run(users.filter(_.name === name).take(1).result).map(_.headOption).map(user => Ok(Json.toJson(user)))
  }

  def getTeams() = Action.async {
    db.run(players.map(_.user).distinct.result).map(teams => Ok(Json.toJson(teams)))
  }

  def getPlayers(user: String) = Action.async {
    db.run(players.filter(_.user === user).result).map(players => Ok(Json.toJson(players)))
  }

  def getAllPlayers() = Action.async {
    db.run(players.result).map(players => Ok(Json.toJson(players)))
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

//  // Usage of CouchbaseAction
//  def getUser(key: String) = CouchbaseAction("default") { bucket =>
//    bucket.get[JsObject](key).map { maybeUser =>
//      maybeUser
//        .map(user => Ok(user))
//        .getOrElse(BadRequest(s"Unable to find user with key: $key"))
//    }
//  }
//
//  // Usage of N1QL plugin
//  def findUserByEmail(email: String) = CouchbaseAction("default") { implicit bucket =>
//    N1QL( s""" SELECT id, name, email FROM default WHERE email = '${email}' """ ).toList[User].map { users =>
//      Ok(users.mkString(" | "))
//    }
//  }
}
