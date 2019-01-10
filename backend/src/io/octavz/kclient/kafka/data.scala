package io.octavz.kclient.kafka

import org.apache.kafka.clients.admin._
import org.apache.kafka.common.TopicPartitionInfo
import org.apache.kafka.common.Node
import scala.collection.JavaConverters._

package object data {

  sealed trait TopicReplication

  case class ByPartitions(numPartitions: Int = 3, replicationFactor: Short = 1) extends TopicReplication

  case class ByAssignment(assignment: Map[Integer, List[Integer]]) extends TopicReplication

  sealed trait ConsumerGroup

  case class ConsumerGroupSimple(name: String, isSimple: Boolean) extends ConsumerGroup

  object ConsumerGroupSimple {
    def apply(c: ConsumerGroupListing): ConsumerGroupSimple =  ConsumerGroupSimple(c.groupId(), c.isSimpleConsumerGroup)
  }

  type TopicName = String

  case class Topic(name: TopicName, replication: TopicReplication = ByPartitions())

  case class Broker(id: Int, host: String, port: Long, rack: String)

  object Broker {
    def apply(node: Node): Broker = Broker(node.id(), node.host(), node.port(), node.rack())

    def fromList(lst: java.util.List[Node]): List[Broker] = lst.asScala.map(apply).toList
  }

  case class Partition(partition: Int, isr: List[Broker], leader: Broker, replicas: List[Broker])

  object Partition {

    def apply(p: TopicPartitionInfo): Partition =
      Partition(partition = p.partition(),
        isr = Broker.fromList(p.isr()),
        leader = Broker(p.leader()),
        replicas = Broker.fromList(p.replicas()))
  }


}
