package io.github.octavz.kclient.actions

import org.specs2.mutable.Specification
import io.github.octavz.kclient.AppEnv
import io.github.octavz.kclient.Helpers._

class TopicActionsSpec extends Specification {

  "Topic Actions" >> {
    "return topics" >> {
      returnTopics()
    }
    "return partitions" >> {
      returnPartitions()
    }
  }

  val testEnv = AppEnv(Seq(), Seq())

  private[this] def returnTopics() =
    new TopicActions().topics()
      .run(testEnv).unsafeRunSync().head.name must contain("topic")

  private[this] def returnPartitions() =
    new TopicActions().partitions("test")
      .run(testEnv).unsafeRunSync().head.name must contain("test")


}
