package io.octavz

import cats.data.ReaderT
import cats.effect._
import io.octavz.kclient.data._

package object kclient {

  type AppRun[A] = ReaderT[IO, AppEnv, A]

  def emptyState = ST(List.empty[String])

  def liftF[A](c: IO[A]): ReaderT[IO, AppEnv, A] = ReaderT.liftF[IO, AppEnv, A](c)

}
