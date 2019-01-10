package io.octavz.kclient

import org.apache.kafka.common.KafkaFuture
import scala.concurrent.duration.Duration
import cats.effect._
import io.octavz.kclient.common.data._

object JavaConverters {


  implicit class KafkaFutureHelper[A](kf: KafkaFuture[A]) {

    def toIO(timeout: Duration = AppEnv.DEFAULT_TIMEOUT): IO[A] = IO(kf.get(timeout.length, timeout.unit))

  }

  implicit class FiniteDurationHelper(fd: Duration) {
    def toMs = fd.toMillis.toInt
  }


}
