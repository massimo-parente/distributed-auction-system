package actors

import org.scalatestplus.play._

import scala.concurrent.ExecutionContext.Implicits.global
import org.reactivecouchbase.ReactiveCouchbaseDriver
import org.scalatest.BeforeAndAfterAll

/**
  * Created by massimo on 01/10/16.
  */
class Models extends PlaySpec with BeforeAndAfterAll {

  // get a driver instance driver
  val driver = ReactiveCouchbaseDriver()
  // get the default bucket
  val bucket = driver.bucket("default")

  override def afterAll(): Unit = {
    driver.shutdown()
  }

  "someone" must  {

    "do something" in {

    }

  }
}
