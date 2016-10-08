package models


import org.scalatestplus.play._
import org.scalatest.concurrent.ScalaFutures
import testhelpers.{DatabaseFixturesConfig, Injector}


import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by massimo on 04/10/16.
  */
class ModelsSpec extends PlaySpec with ScalaFutures with OneAppPerTest {

  import testhelpers.Injector

  val repo = Injector.inject[CouchbaseUserRepository]

  "CouchbaseUserRepository" should {

    "create and retrieve user" in {

      val save = repo.create(User("max", "tricheco", "admin", 100))

      val find = save flatMap { res =>
        repo.findById("max")
      }

      whenReady(find) { res =>
        res.get.name mustBe("max")
      }
    }

    "find all" in {

      repo.create(User("user1", "team1", "admin", 100))
      repo.create(User("user2", "team2", "bidder", 100))
      repo.create(User("user2", "team3", "bidder", 100))

      val f = repo.findAll()

      whenReady(f) { res =>

        println(res)

      }
    }

  }
}
