package io.octavz.kclient

import cats.effect._
import cats.implicits._

import cats.effect.concurrent.Ref

import org.http4s.HttpApp
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._

import com.olegpy.meow.effects._
import io.chrisdavenport.log4cats.Logger

import io.octavz.kclient.kafka.KafkaOps
import io.octavz.kclient.common.data._
import io.octavz.kclient.common.implicits._

object KClientServer extends IOApp {

  def run(args: List[String]): IO[ExitCode] = AppEnv.empty().flatMap { env =>
    Ref.unsafe[IO, AppEnv](env).runAsk { implicit askInst =>
      ServerStream.stream[IO].compile.drain.as(ExitCode.Success)
    }
  }
}

object ServerStream {

  def httpApp[F[_] : Effect : KafkaOps]: HttpApp[F] = {
    val adminRoutes = AdminRoutes[F]
    Router.define(
      "/" -> adminRoutes.topicRoutes
    )(adminRoutes.topicRoutes).orNotFound
  }

  def stream[F[_] : ConcurrentEffect : Timer : Logger : KafkaOps]: fs2.Stream[F, ExitCode] = {
    BlazeServerBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(httpApp[F])
      .serve
  }
}
