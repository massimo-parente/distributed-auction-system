package config

import javax.inject.Singleton

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import org.slf4j.LoggerFactory

/**
  * Created by massimo on 29/08/16.
  */
@Singleton
class DatabaseConfig @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  import models._

  private val logger = LoggerFactory.getLogger("config.DatabaseConfig")

  val users = TableQuery[Users]
  val players = TableQuery[Players]

  val db = dbConfigProvider.get[JdbcProfile].db

  val setupFuture: Future[Unit] = {
    logger.info("Creating database schema...")
    db.run(
      DBIO.seq(
        users.schema.create,
        players.schema.create,
        users += User("max", UserRoles.ADMIN, 0),
        users += User("gino", UserRoles.BIDDER, 0)
      )
    )
  }

  // block until database created
  Await.result(setupFuture, Duration.Inf)
}
