package de.gitzoz.akkastreamkafkasample.domain

import java.util.concurrent.atomic.AtomicLong

import scala.concurrent.Future

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import play.api.libs.json.Json

import de.gitzoz.akkastreamkafkasample.domain.MessagesOps.clickedReads
import akka.kafka.scaladsl.Consumer
import akka.kafka.Subscriptions
import org.apache.kafka.common.TopicPartition
import akka.stream.scaladsl.Sink
import akka.stream.Materializer

object MessageConsumer {
  def settings(system: ActorSystem) = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
}

class ClickedConsumer(actorSystem: ActorSystem)(implicit materializer: Materializer) {
  var offset = new AtomicLong
  val records: scala.collection.mutable.ListBuffer[Clicked] = scala.collection.mutable.ListBuffer.empty

  def store(record: ConsumerRecord[Array[Byte], String]): Future[Done] = {
    val clicked = Json.parse(record.value).as[Clicked]
    records.append(clicked)
    offset.set(record.offset)
    Future.successful(Done)
  }

  def subscription(topic: String, partition: Int = 0) = Subscriptions.assignmentWithOffset(new TopicPartition(topic, partition) -> offset.get)

  def runConsumer: Future[Done] = Consumer.plainSource(MessageConsumer.settings(actorSystem), subscription("clicked"))
    .mapAsync(1)(store(_))
    .runWith(Sink.ignore)
}