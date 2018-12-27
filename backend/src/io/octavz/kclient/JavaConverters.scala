package io.octavz.kclient

import org.apache.kafka.common.KafkaFuture
import scala.concurrent.duration.Duration
import cats.effect._
import io.octavz.kclient.data._

object JavaConverters {
  implicit class KafkaFutureHelper[A](kf: KafkaFuture[A]) {

    def toIO(timeout: Duration = AppEnv.DEFAULT_TIMEOUT): IO[A] =
      Async[IO].async { cb =>
        kf.whenComplete((a: A, t: Throwable) =>
          if (a == null) cb(Left(t)) else cb(Right(a)))
      }
  }

}
