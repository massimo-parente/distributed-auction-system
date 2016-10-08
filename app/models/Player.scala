package models

import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by mparente on 20/09/2016.
  */
case class Player(name: String, role: String, value: Int, team: String, user: Option[String])

object Players {
  implicit val playersFormat = Json.format[Player]
}

trait PlayerRepository { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  class Players(tag: Tag) extends Table[Player](tag, "PLAYERS") {
    def name: Rep[String] = column[String]("NAME", O.PrimaryKey)
    def role: Rep[String] = column[String]("ROLE")
    def value: Rep[Int] = column[Int]("VALUE")
    def team: Rep[String] = column[String]("TEAM")
    def user: Rep[String] = column[String]("USER" )
    def * = (name, role, value, team, user.?) <> (Player.tupled, Player.unapply)
  }

  def find(name: String): Future[Option[Player]]
  def findAll(): Future[Seq[Player]]
  def findPlayersOfUser(name: String): Future[Seq[Player]]
  def signPlayer(player: String, user: String, value: Int): Future[Int]
  def addPlayer(player: Player): Future[Unit]
}

@Singleton
class PlayerRepositoryImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends PlayerRepository
    with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val players = TableQuery[Players]

  def find(name: String): Future[Option[Player]] = {
    db.run(players.filter(_.name === name).result.headOption)
  }

  override def findAll() = {
    db.run(players.result)
  }

  override def findPlayersOfUser(name: String) = {
    db.run(players.filter(_.user === name).result)
  }

  override def signPlayer(player: String, user: String, value: Int): Future[Int] = {
    db.run(
      players.filter(_.name === player).map(p => (p.user, p.value)).update(user, value)
    )
  }

  override def addPlayer(player: Player): Future[Unit] = {
    db.run(DBIO.seq(players += player))
  }
}
