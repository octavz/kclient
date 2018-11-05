package io.github.octavz.kclient
package actions

import cats.effect.IO

import data._
import Helpers._

class TopicActions {

  def topics(): AppRun[List[Topic]] = for {
    env <- ask
    ret <- {
      println(s"connecting to ${env.kafkaBrokers}")
      liftF(IO(List(Topic("test-topic-1"), Topic("test-topic-2"))))
    }
  } yield ret


  def partitions(topicName: String): AppRun[List[Partition]] = for {
    env <- ask
    ret <- {
      println(s"connecting to ${env.kafkaBrokers}")
      liftF(IO(List(Partition(s"$topicName-p-1"),
        Partition(s"$topicName-p-2"))))
    }
  } yield ret

}
