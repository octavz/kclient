package io.octavz.kclient.kafka

import cats._, cats.implicits._
import cats.mtl._
import cats.effect._
import kafka.zk.KafkaZkClient
import io.chrisdavenport.log4cats._
import org.apache.kafka._, common._, config._, clients.admin._
import simulacrum.typeclass

import io.octavz.kclient.common.data._

@typeclass
trait KafkaEnabled[F[_]] {
  def withKafka[A](call: AdminClient => F[A]): F[A]

  def withKafkaIO[A](call: AdminClient => IO[A]): F[A]

  def withZk[A](call: KafkaZkClient => F[A]): F[A]
}

object KafkaEnabled {
  implicit def instF[F[_] : Sync : Logger](implicit AA: ApplicativeAsk[F, AppEnv]): KafkaEnabled[F] = new KafkaEnabled[F] {

    override def withKafka[A](call: AdminClient => F[A]): F[A] = for {
      env <- AA.ask
      _ <- Logger[F].trace(s"${env.kafkaConfig}")
      res <- call(AdminClient.create(env.kafkaConfig.asProperties))
    } yield res

    override def withKafkaIO[A](call: AdminClient => IO[A]): F[A] =
      withKafka(call andThen liftIO)

    override def withZk[A](call: KafkaZkClient => F[A]): F[A] = for {
      env <- AA.ask
      _ <- Logger[F].trace(s"${env.kafkaConfig}")
      res <- call(KafkaZkClient(env.zkConfig.stringHosts,
        env.zkConfig.isSecure,
        env.zkConfig.sessionTimeoutMs,
        env.zkConfig.connectionTimeoutMs,
        env.zkConfig.maxInFlightRequests,
        env.time))
    } yield res

  }

}
