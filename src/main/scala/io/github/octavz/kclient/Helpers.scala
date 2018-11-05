package io.github.octavz.kclient

import cats.effect.IO
import cats.data._

import io.github.octavz.kclient.actions.TopicActions

case class AppEnv(kafkaBrokers: Seq[String], zookeeperBrokers: Seq[String], adminService: TopicActions = new TopicActions())

object Helpers {

  type AppRun[A] = ReaderT[IO, AppEnv, A]

  def ask: ReaderT[IO, AppEnv, AppEnv] = ReaderT.ask[IO, AppEnv]

  def liftF[A](c: IO[A]): ReaderT[IO, AppEnv, A] = ReaderT.liftF[IO, AppEnv, A](c)

}
