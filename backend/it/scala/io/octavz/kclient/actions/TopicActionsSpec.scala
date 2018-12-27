package io.octavz.kclient.actions

import io.github.octavz.kclient._
import cats.mtl.implicits._
import com.dimafeng.testcontainers.ForAllTestContainer
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import io.github.octavz.kclient.data.Topic
import implicits._
import data._

class TopicActionsSpec extends FlatSpec with Matchers with ForAllTestContainer {

  import com.dimafeng.testcontainers.GenericContainer

  val testEnv = AppEnv(KafkaConfig("", Seq(), Seq()))


  "Topic Actions" should "return topics" in {
    KafkaAdmin[AppRun].topics()
      .run(testEnv).unsafeRunSync() should contain(Topic("test-topic-1"))
  }

  it should "return partitions" in {
    KafkaAdmin[AppRun]().partitions("test")
      .run(testEnv).unsafeRunSync().head.name should contain("test")
  }
  override val container =
}
