package models

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Created by mparente on 20/09/2016.
  */
case class Player(name: String, role: String, value: Int, team: String, user: String)

object Players {
  implicit val playersFormat = Json.format[Player]
}

class Players(tag: Tag) extends Table[Player](tag, "PLAYERS") {
  def name: Rep[String] = column[String]("NAME", O.PrimaryKey)
  def role: Rep[String] = column[String]("ROLE")
  def value: Rep[Int] = column[Int]("VALUE")
  def team: Rep[String] = column[String]("TEAM")
  def user: Rep[String] = column[String]("USER")
  def * = (name, role, value, team, user) <> (Player.tupled, Player.unapply)
}


trait PlayerRepository {
  def find(name: String): Future[Option[Player]]
  def findAll(): Future[Seq[Player]]
  def signPlayer(player: String, user: String, value: Int): Future[Int]
  def addPlayer(player: Player): Future[Unit]
}

class PlayerRepositoryImpl @Inject()(dbConfigProvider: DatabaseConfigProvider) extends PlayerRepository {
  val db = dbConfigProvider.get[JdbcProfile].db
  val players = TableQuery[Players]

  def find(name: String): Future[Option[Player]] = {
    db.run(players.filter(_.name === name).result.headOption)
  }

  override def findAll() = {
    db.run(players.result)
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
