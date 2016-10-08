package models

import javax.inject.Inject

import com.google.inject.Singleton
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc.Action
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case object UserRoles {
  val ADMIN = "admin"
  val BIDDER = "bidder"
}

case class User(name: String, teamName: String, role: String, budget: Int)

object Users {
  implicit val usersFormat = Json.format[User]
}

trait UserRepository { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  class Users(tag: Tag) extends Table[User](tag, "USERS") {
    def name: Rep[String] = column[String]("NAME", O.PrimaryKey)
    def teamName: Rep[String] = column[String]("TEAM_NAME")
    def role: Rep[String] = column[String]("ROLE")
    def budget: Rep[Int] = column[Int]("BUDGET")
    def * = (name, teamName, role, budget) <> (User.tupled, User.unapply)
  }

  def findAll(): Future[Seq[User]]
  def getUser(name: String): Future[Option[User]]
  def getUserNames(): Future[Seq[String]]
  def getBudget(user: String): Future[Int]
  def updateBudget(user: String, budget: Int): Future[Int]
  def addUser(user: User): Future[Unit]
  def updateUser(name: String, role: String, budget: Int): Future[Unit]
  def deleteUser(name: String): Future[Unit]
}

@Singleton
class UserRepositoryImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends UserRepository
    with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val users = TableQuery[Users]

  override def findAll() = {
    db.run(users.result)
  }

  override def getUser(name: String) = {
    db.run(users.filter(_.name === name).take(1).result).map(_.headOption)
  }

  override def getUserNames() = {
    db.run(users.map(_.name).result)
  }

  override def getBudget(user: String) = {
    db.run(users.filter(_.name === user).take(1).result).map(_.head.budget)
  }

  override def updateBudget(user: String, budget: Int) = {
    db.run(users.filter(_.name === user).map(_.budget).update(budget))
  }

  override def addUser(user: User) = {
    db.run(users += user).map(_ => ())
  }

  override def updateUser(name: String, role: String, budget: Int) = {
    db.run(
      users.filter(_.name === name)
        .map(user => (user.budget, user.role))
        .update(budget, role)
    ).map(_ => ())
  }

  override def deleteUser(name: String) = {
    db.run(users.filter(_.name === name).delete).map(_ => ())
  }


}