package io.octavz.kclient

import cats.effect._
import cats.implicits._
import org.http4s.HttpApp
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._

import io.octavz.kclient.common.data._

object KClientServer extends IOApp {
  def run(args: List[String]): IO[ExitCode] = AppEnv().flatMap { env =>
    ServerStream.stream(env).compile.drain.as(ExitCode.Success)
  }
}

object ServerStream {

  def httpApp[F[_]: Effect: ContextShift: Timer]: HttpApp[F] =
    Router(
      "/" -> AdminService[F].routes
    ).orNotFound

  def stream(appEnv: AppEnv): fs2.Stream[IO, ExitCode] = {
    val ec = scala.concurrent.ExecutionContext.Implicits.global
    implicit val cs: ContextShift[IO] = IO.contextShift(ec)
    implicit val timer: Timer[IO] = IO.timer(ec)
    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(httpApp[IO])
      .serve
  }
}
