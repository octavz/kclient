package io.octavz.kclient.common

import cats.Monoid
import io.chrisdavenport.log4cats._, scribe.ScribeLogger
import cats.effect.Sync
import io.octavz.kclient.common.data._

object implicits {


  implicit def stMonoidInstance: Monoid[ST] = new Monoid[ST] {
    override def empty = ST(List.empty)
    override def combine(x: ST, y: ST) = ST(List.concat(x.data, y.data))
  }

  implicit def loggerAppRunInstance[F[_] : Sync]: Logger[F] = ScribeLogger.empty[F]

}
