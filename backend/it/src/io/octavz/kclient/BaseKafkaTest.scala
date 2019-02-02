package io.octavz.kclient

import cats.effect.concurrent.Ref
import com.dimafeng.testcontainers.DockerComposeContainer
import org.scalatest.FlatSpec
import scala.util.Random

import com.olegpy.meow.effects._
import java.io.File
import eu.timepit.{refined => R}

import org.scalatest.Matchers
import cats.effect.IO
import com.dimafeng.testcontainers.ForAllTestContainer
import io.octavz.kclient.common.data._
import io.octavz.kclient.common.implicits._

trait BaseKafkaTest extends FlatSpec with Matchers with ForAllTestContainer {

  val rand = new Random()

  def port() = 30000 + rand.nextInt(10000)

  def guid() = java.util.UUID.randomUUID().toString

  val (p1, p2, p3, pz) = (port(), port(), port(), 21810)

  override val container = DockerComposeContainer(
    composeFiles = new File("backend/it/resources/docker-compose.yml"),
    env = Map("ZOO_PORT" -> s"$pz", "BROKER_PORT_1" -> s"$p1", "BROKER_PORT_2" -> s"$p2", "BROKER_PORT_3" -> s"$p3")
  )

  def admin(implicit env: AppEnv) =
    Ref.unsafe[IO, AppEnv](env).runAsk { implicit askInst =>
      import io.octavz.kclient.kafka.KafkaOps
      KafkaOps[IO]
    }

  private def host(p: Int, host: String = "localhost"): Host =
    R.refineV[HostValidation](s"$host:$p").right.get

  implicit val testEnv: AppEnv = AppEnv(
    KafkaConfig("test", List(host(p1), host(p2), host(p3))), ZkConfig(List(host(pz))))


}
