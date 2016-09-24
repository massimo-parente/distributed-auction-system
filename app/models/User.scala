package models

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case object UserRoles {
  val ADMIN = "admin"
  val BIDDER = "bidder"
}

case class User(name: String, role: String, budget: Int)

object Users {
  implicit val usersFormat = Json.format[User]
}

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def name: Rep[String] = column[String]("NAME", O.PrimaryKey)

  def role: Rep[String] = column[String]("ROLE")

  def budget: Rep[Int] = column[Int]("BUDGET")

  def * = (name, role, budget) <> (User.tupled, User.unapply)
}

trait UserRepository {
  def findAll(): Future[Seq[User]]
  def getUserNames(): Future[Seq[String]]
  def getBudget(user: String): Future[Int]
  def updateBudget(user: String, budget: Int): Future[Int]
}

class UserRepositoryImpl @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends UserRepository {
  val db = dbConfigProvider.get[JdbcProfile].db
  val users = TableQuery[Users]

  override def findAll() = {
    db.run(users.result)
  }

  override def getUserNames() = {
    db.run(users.map(_.name).result)
  }

  def getBudget(user: String) = {
    db.run(users.filter(_.name === user).take(1).result).map(_.head.budget)
  }

  def updateBudget(user: String, budget: Int) = {
    db.run(users.filter(_.name === user).map(_.budget).update(budget))
  }
}
