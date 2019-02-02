package io.octavz.kclient.kafka

import cats.implicits._
import cats.mtl._
import cats.effect._
import io.chrisdavenport.log4cats._
import org.apache.kafka._, common._, config._, clients.admin._
import simulacrum.typeclass
import scala.collection.JavaConverters._

import io.octavz.kclient.common.data._
import io.octavz.kclient.JavaConverters._
import io.octavz.kclient.kafka.data._

@typeclass
trait KafkaOps[F[_]] {

  def createTopic(topic: Topic): F[Topic]

  def topics(): F[Set[TopicName]]

  def configBrokers(): F[Map[String, String]]

  def partitionsFor(topic: String): F[List[Partition]]

  def consumerGroups(): F[List[ConsumerGroupSimple]]
}

object KafkaOps {
  implicit def instF[F[_] : Sync : Logger : LiftIO : KafkaEnabled](implicit AA: ApplicativeAsk[F, AppEnv]): KafkaOps[F] = new KafkaOps[F] {

    val K = KafkaEnabled[F]

    override def createTopic(topic: Topic): F[Topic] = K.withKafkaIO {
      val newTopic = topic.replication match {
        case ByPartitions(num, factor) => new NewTopic(topic.name, num, factor)
        case ByAssignment(data)        => new NewTopic(topic.name, data.map { case (k, v) => (k, v.asJava) }.asJava)
      }
      _.createTopics(Seq(newTopic).asJava).all().toIO().map(_ => topic)
    }

    override def topics(): F[Set[TopicName]] = K.withKafkaIO {
      val options = new ListTopicsOptions()
        .listInternal(true)
        .timeoutMs(AppEnv.DEFAULT_TIMEOUT_MS)
      _.listTopics(options).names().toIO().map(_.asScala.toSet)
    }

    override def consumerGroups(): F[List[ConsumerGroupSimple]] = K.withKafkaIO {
      val options = new ListConsumerGroupsOptions()
        .timeoutMs(AppEnv.DEFAULT_TIMEOUT_MS)
      _.listConsumerGroups(options).all().toIO()
        .map(_.asScala.map(ConsumerGroupSimple.apply).toList)
    }

    override def configBrokers(): F[Map[String, String]] = K.withKafkaIO {
      val options = new DescribeConfigsOptions()
        .timeoutMs(AppEnv.DEFAULT_TIMEOUT_MS)
        .includeSynonyms(true)
      val confResource = new ConfigResource(ConfigResource.Type.BROKER, "0")
      _.describeConfigs(Seq(confResource).asJava, options)
        .all().toIO()
        .map(_.values().asScala.headOption)
        .map {
          case Some(v) => v.entries().asScala.map(e => (e.name(), e.value())).toMap
          case _       => Map.empty[String, String]
        }
    }

    override def partitionsFor(topic: String): F[List[Partition]] = K.withKafka {
      _.describeTopics(List(topic).asJava).values().asScala.headOption match {
        case Some((_, v)) =>
          LiftIO[F].liftIO(v.toIO().map(_.partitions().asScala.map(Partition.apply).toList))
        case _            =>
          Logger[F].info(s"Topic $topic not found").flatMap(_ => Sync[F].raiseError(TopicNotFoundError))
      }

    }

  }

}
