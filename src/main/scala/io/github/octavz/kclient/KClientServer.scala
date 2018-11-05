package io.github.octavz.kclient

import cats.effect._
import fs2.StreamApp
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext
import Helpers._

object KClientServer extends StreamApp[IO] {

  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]) = {
    ServerStream.stream
  }
}

object ServerStream {
  val appEnv = AppEnv(Seq(), Seq())

  def stream(implicit ec: ExecutionContext) = {
    val mount = new TopicsService(appEnv).service

    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(mount)
      .serve
  }

}
