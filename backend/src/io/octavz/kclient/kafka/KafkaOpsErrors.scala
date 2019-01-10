package io.octavz.kclient.kafka

sealed trait KafkaError extends Throwable {
  val message: String

  override def getMessage = message
}

case object TopicNotFoundError extends KafkaError {
  override val message = "Topic not found"
}
