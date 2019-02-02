package io.octavz.kclient.common

import java.util.Properties
import scala.concurrent.duration._
import cats.effect.IO

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.utils._

import pureconfig.generic.auto._
import pureconfig.error.ConfigReaderFailures

import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.pureconfig._
import eu.timepit.refined.string._

package object data {

  val W = shapeless.Witness
  type HostValidation = MatchesRegex[W.`"[^:]+:[0-9]+"`.T]
  type Host = String Refined HostValidation

  case class ZkConfig(
    hosts: List[Host],
    isSecure: Boolean = false,
    sessionTimeoutMs: Int = 1000,
    connectionTimeoutMs: Int = 1000,
    maxInFlightRequests: Int = 100) {

    def stringHosts = hosts.mkString(",")
  }

  case class KafkaConfig(groupId: String,
    brokers: List[Host],
    clientId: String = "consumerAdmin") {

    val asProperties: Properties = {
      val props = new Properties()
      props.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers.mkString(","))
      props.setProperty(AdminClientConfig.CLIENT_ID_CONFIG, clientId)
      props.setProperty(AdminClientConfig.METADATA_MAX_AGE_CONFIG, "3000")
      props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
      props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
      props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
      props.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
      props
    }

    override def toString =
      s"KafkaConfig: \n\t groupId: $groupId \n\t brokers: ${brokers.mkString(",")} \n"
  }

  case class AppEnv(kafkaConfig: KafkaConfig, zkConfig: ZkConfig, time: Time = new SystemTime)


  sealed trait GenericError extends Throwable {
    val message: String

    override def getMessage = message
  }

  case class ConfigError(e: ConfigReaderFailures) extends GenericError {
    override val message = e.toList.map(_.description).mkString("\n")
  }

  object AppEnv {

    val DEFAULT_TIMEOUT = 10.seconds
    val DEFAULT_TIMEOUT_MS = DEFAULT_TIMEOUT.toMillis.toInt

    def empty() : IO[AppEnv] = IO.pure(AppEnv(
      kafkaConfig = KafkaConfig("kclient",List("localhost:9092")),
      zkConfig = ZkConfig(List("localhost:2181"))))



    def apply(): IO[AppEnv] = for {
      confKafka <- pureconfig.loadConfig[KafkaConfig]("kclient.kafka") match {
        case Right(c) => IO.pure(c)
        case Left(e)  => IO.raiseError(ConfigError(e))
      }
      confZk <- pureconfig.loadConfig[ZkConfig]("kclient.zookeeper") match {
        case Right(c) => IO.pure(c)
        case Left(e)  => IO.raiseError(ConfigError(e))
      }
    } yield AppEnv(confKafka, confZk)

  }

  case class ST(data: List[String]) extends AnyVal

}
