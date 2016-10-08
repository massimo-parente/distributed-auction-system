package models

import javax.inject.Inject

import com.google.inject.Singleton
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class Event(id: Option[Int], payload: String, savePoint: Boolean)

trait EventRepository { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  class Events(tag: Tag) extends Table[Event](tag, "EVENTS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def payload = column[String]("PAYLOAD")
    def savePoint = column[Boolean]("SAVE_POINT")
    def * = (id.?, payload, savePoint) <> (Event.tupled, Event.unapply)
  }

  def add(event: Event): Future[Unit]
  def findFromLatestSavePoint(): Future[Seq[Event]]
}

@Singleton
class EventRepositoryImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends EventRepository
    with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

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
