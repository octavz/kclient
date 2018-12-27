package io.octavz.kclient.data

import java.util.Properties
import org.apache.kafka.clients.admin.AdminClientConfig
import scala.concurrent.duration.Duration
import cats.effect._
import pureconfig.generic.auto._

case class Topic(name: String)

case class Partition(name: String)

case class KafkaConfig(groupId: String, brokers: Seq[String], zookeeperHosts: Seq[String]) {

  val asProperties: Properties = {
    import org.apache.kafka.clients.consumer.ConsumerConfig
    val props = new Properties()
    props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers.mkString(","))
    props.setProperty(AdminClientConfig.CLIENT_ID_CONFIG, "consumerAdmin")
    props.setProperty(AdminClientConfig.METADATA_MAX_AGE_CONFIG, "3000")
    props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
    props
  }

}

case class AppEnv(kafkaConfig: KafkaConfig)

object AppEnv {

  val DEFAULT_TIMEOUT = Duration.Inf

  def apply(): IO[AppEnv] =
     pureconfig.loadConfig[KafkaConfig]("kclient.kafka") match {
       case Right(c) => IO.pure(AppEnv(c))
       case Left(e) => IO.raiseError(new Exception(e.toList.map(_.description).mkString("\n")))
     }


}

case class ST(data: List[String]) extends AnyVal
