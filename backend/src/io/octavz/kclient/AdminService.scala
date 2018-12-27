package io.octavz.kclient

import io.circe.Json
import org.http4s.HttpRoutes
import org.http4s.server.Router
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import cats.effect._
import cats.mtl.implicits._

import io.octavz.kclient.actions.KafkaOps
import io.octavz.kclient.implicits._

class AdminService[F[_]](implicit F: Effect[F], cs: ContextShift[F]) extends Http4sDsl[F] {

  val admin = implicitly[KafkaOps[AppRun]]

  def routes(implicit timer: Timer[F]): HttpRoutes[F] =
    Router[F](
      "/topics" -> topicRoutes,
      "/consumers" -> consumerRoutes
    )

  val topicRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok(Json.obj("message" -> Json.fromString(s"Topics")))
      case GET -> Root /  name =>
        Ok(Json.obj("message" -> Json.fromString(s"Topic: ${name}")))
    }

  val consumerRoutes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok(Json.obj("message" -> Json.fromString(s"Consumers")))
    }
}

object AdminService {

  def apply[F[_]: Effect: ContextShift]: AdminService[F] = new AdminService[F]

}



