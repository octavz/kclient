package io.octavz.kclient.actions

import cats._
import cats.implicits._
import cats.mtl._
import cats.effect._
import io.chrisdavenport.log4cats.Logger
import org.apache.kafka.clients.admin._
import simulacrum.typeclass

import io.octavz.kclient.data._
import io.octavz.kclient.JavaConverters._

@typeclass
trait KafkaOps[F[_]] {
  def topics(): F[List[Topic]]

  def partitionsFor(topic: String): F[List[Partition]]
}

object KafkaOps {
  implicit def instF[F[_] : Sync : Logger](implicit AA: ApplicativeAsk[F, AppEnv]): KafkaOps[F] = new KafkaOps[F] {
    private val adminClient: F[AdminClient] = AA.reader(env => AdminClient.create(env.kafkaConfig.asProperties))

    override def topics(): F[List[Topic]] = for {
      env <- AA.ask
      client <- adminClient
      ret <- {
        val options = new ListTopicsOptions()
        options.listInternal(true)
        val res = client.listTopics(options).namesToListings().toIO()
        Monad[F].pure(List(Topic("test-topic-1"), Topic("test-topic-2")))
      }
      _ <- Logger[F].info(s"Connecting to ${env.kafkaConfig.brokers}")
    } yield ret


    override def partitionsFor(topic: String): F[List[Partition]] = for {
      env <- AA.ask
      ret <- {
        Monad[F].pure(List(Partition(s"$topic-p-1"), Partition(s"$topic-p-2")))
      }
      _ <- Logger[F].info(s"Connecting to ${env.kafkaConfig.brokers}")
    } yield ret

  }

}
