package io.github.octavz.kclient
package actions

import cats.effect.IO

import data._


class TopicActions {

  def topics(): IO[List[Topic]] =
    IO(List(Topic("test-topic-1"), Topic("test-topic-2")))

  def partitions(topicName: String): IO[List[Partition]] =
    IO(List(Partition(s"$topicName-p-1"), Partition(s"$topicName-p-2")))

}
