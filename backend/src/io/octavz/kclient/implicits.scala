package io.octavz.kclient

import cats._
import cats.effect._
import io.chrisdavenport.log4cats.scribe.ScribeLogger
import io.chrisdavenport.log4cats.Logger

import io.octavz.kclient.data._

object implicits {

  implicit def stMonoidInstance: Monoid[ST] = new Monoid[ST] {
    override def empty = ST(List.empty)
    override def combine(x: ST, y: ST) = ST(List.concat(x.data, y.data))
  }

  implicit def loggerAppRunInstance[F[_] : Sync]: Logger[F] = ScribeLogger.empty[F]

}

