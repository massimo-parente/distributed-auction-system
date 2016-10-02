package models

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class Event(id: Option[Int], payload: String, savePoint: Boolean)

class Events(tag: Tag) extends Table[Event](tag, "EVENTS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def payload = column[String]("PAYLOAD")
  def savePoint = column[Boolean]("SAVE_POINT")
  def * = (id.?, payload, savePoint) <> (Event.tupled, Event.unapply)
}

trait EventRepository {
  def add(event: Event): Future[Unit]
  def findFromLatestSavePoint(): Future[Seq[Event]]
}

class EventRepositoryImpl @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends EventRepository {
  val db = dbConfigProvider.get[JdbcProfile].db
  val events = TableQuery[Events]

  override def add(event: Event): Future[Unit] = {
    db.run(DBIO.seq(events += event))
  }

  override def findFromLatestSavePoint(): Future[Seq[Event]] = {
    db.run {
      val id = events.filter(_.savePoint === true).map(_.id).max.getOrElse(0)
      events.filter(_.id >= id).result
    }
  }

}
