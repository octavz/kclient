package io.octavz.kclient.data

import org.scalatest.FlatSpec
import org.scalatest.Matchers

class DataSpec extends FlatSpec with Matchers {
  val testEnv = AppEnv(KafkaConfig("group", Seq("broker-1","broker-2"), Seq("zoo-1")))

  "KafkaConfig" should "be correctly read as Properties" in {
   val res = testEnv.kafkaConfig.asProperties
    res.getProperty("bootstrap.servers") shouldBe "broker-1,broker-2"
    res.getProperty("group.id") shouldBe "group"
  }

}
