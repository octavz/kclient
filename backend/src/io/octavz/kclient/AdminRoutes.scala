package io.octavz.kclient

import io.circe.Json
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import cats.effect._

import io.octavz.kclient.kafka.KafkaOps

class AdminRoutes[F[_] : Sync](implicit K: KafkaOps[F]) extends Http4sDsl[F] {

  val topicRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root        =>
        Ok(Json.obj("message" -> Json.fromString(s"Topics")))
      case GET -> Root / name =>
        Ok(Json.obj("message" -> Json.fromString(s"Topic: ${name}")))
    }

  val consumerRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok(Json.obj("message" -> Json.fromString(s"Consumers")))
    }
}

object AdminRoutes {

  def apply[F[_] : Sync : KafkaOps]: AdminRoutes[F] = new AdminRoutes[F]

}



