package io.octavz.kclient.kafka

import io.octavz.kclient._, kafka.data._

class KafkaOpsTest extends BaseKafkaTest {

  "KafkaOps" should "create and list topics" in {
    val randomTopic = guid()
    val io = for {
      _ <- admin.createTopic(Topic(randomTopic))
      r <- admin.topics()
    } yield r
    val actual = io.unsafeRunSync()
    actual.size shouldBe 1
    actual.head shouldBe randomTopic
  }

  it should "list partitions" in {
    val randomTopic = guid()
    val io = for {
      _ <- admin.createTopic(Topic(randomTopic, ByPartitions(12, 3)))
      r <- admin.partitionsFor(randomTopic)
    } yield r
    val actual = io.unsafeRunSync()
    actual.size shouldBe 12
    actual.head.replicas.size shouldBe 3
  }

/*  it should "retrieve brokers config" in {
    val io = for {
      r <- admin.configBrokers()
    } yield r
    val actual = io.unsafeRunSync()
    println(actual)
    actual.size shouldBe 12
  }*/

  it should "retrieve consumer groups" in {
    val io = for {
      r <- admin.consumerGroups()
    } yield r
    val actual = io.unsafeRunSync()
    println(actual)
    actual.size shouldBe 0
  }

}
