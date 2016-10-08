package testhelpers

import javax.inject.Singleton

import com.google.inject.Inject
import org.slf4j.LoggerFactory
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by massimo on 29/08/16.
  */
@Singleton
class DatabaseFixturesConfig @Inject()(dbConfigProvider: DatabaseConfigProvider) {
  import models._
  private val logger = LoggerFactory.getLogger("config.DatabaseConfig")
  val users = TableQuery[Users]
  val players = TableQuery[Players]

  val db = dbConfigProvider.get[JdbcProfile].db

  val fixtures: Future[Unit] = {
    logger.info("Creating database schema...")
    db.run(
      DBIO.seq(
        users.schema.create,
        players.schema.create,
        users += User("user1", "team1", UserRoles.ADMIN, 0),
        users += User("user2", "team2", UserRoles.BIDDER, 0),
        users += User("user3", "team3", UserRoles.BIDDER, 0),
        players += Player("player1", "M", 0, "team1", Some("user1")),
        players += Player("player2", "M", 0, "team2", Some("user2")),
        players += Player("player3", "M", 0, "team3", Some("user3"))
      )
    )
  }

  // block until database created
  Await.result(fixtures, Duration.Inf)
}
