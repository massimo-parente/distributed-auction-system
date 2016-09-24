package testhelpers

import play.api.inject.guice.GuiceApplicationBuilder

import scala.reflect.ClassTag

/**
  * Created by mparente on 21/09/2016.
  */
object Injector {
  lazy val injector = (new GuiceApplicationBuilder).injector()

  def inject[T: ClassTag]: T = injector.instanceOf[T]
}