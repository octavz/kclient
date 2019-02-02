package io.octavz.kclient.common.data

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import eu.timepit.refined.auto._

class DataSpec extends FlatSpec with Matchers {
  val testEnv = AppEnv(KafkaConfig("group", List("broker-1:9000","broker-2:9001")), ZkConfig(List("zoo-1:2000")))

  "KafkaConfig" should "be correctly read as Properties" in {
   val res = testEnv.kafkaConfig.asProperties
    res.getProperty("bootstrap.servers") shouldBe "broker-1,broker-2"
    res.getProperty("group.id") shouldBe "group"
  }

}
