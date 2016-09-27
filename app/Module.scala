import com.google.inject.AbstractModule
import models._
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {

  import actors._

  override def configure(): Unit = Seq(
    bindActor[AuctionControllerActor]("auctionControllerActor"),
    bind(classOf[UserRepository]).to(classOf[UserRepositoryImpl]),
    bind(classOf[PlayerRepository]).to(classOf[PlayerRepositoryImpl]),
    bind(classOf[EventRepository]).to(classOf[EventRepositoryImpl])
  )
}