package io.github.octavz.kclient

import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import io.circe.syntax._
import io.circe.generic.auto._
import cats.effect._

class TopicsService(env: AppEnv) extends Http4sDsl[IO] {

  val service: HttpService[IO] =
    HttpService[IO] {
      case GET -> Root / "topics"                 =>
        env.adminService.topics()
          .run(env)
          .flatMap(r => Ok(r.asJson))
      case GET -> Root / "partitions" / topicName =>
        env.adminService.partitions(topicName)
          .run(env)
          .flatMap(r => Ok(r.asJson))
    }
}
