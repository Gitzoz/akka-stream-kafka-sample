/*
 * Copyright 2016 Stefan Roehrbein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
  def settings(system: ActorSystem, host: String, port: Int) =
    ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers(s"$host:$port")
      .withGroupId("group1")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
}

abstract class ClickedConsumer(
    actorSystem: ActorSystem,
    startAtOffset: Long,
    host: String,
    port: Int
)(implicit materializer: Materializer) {
  var offset = new AtomicLong(startAtOffset)

  def consume(record: ConsumerRecord[Array[Byte], String]): Future[Done]

  def subscription(topic: String, partition: Int = 0) =
    Subscriptions.assignmentWithOffset(
      new TopicPartition(topic, partition) -> offset.get
    )

  def runConsumer(topic: String): Future[Done] =
    Consumer
      .plainSource(MessageConsumer.settings(actorSystem, host, port),
                   subscription(topic))
      .mapAsync(1)(consume(_))
      .runWith(Sink.ignore)
}

class PrintClickedConsumer(actorSystem: ActorSystem,
                           startAtOffset: Long,
                           host: String,
                           port: Int)(implicit materializer: Materializer)
    extends ClickedConsumer(actorSystem, startAtOffset, host, port) {
  def consume(record: ConsumerRecord[Array[Byte], String]): Future[Done] = {
    val clicked = Json.parse(record.value).as[Clicked]
    offset.set(record.offset)
    println(clicked)
    Future.successful(Done)
  }
}

class CalculateClickedMetricsConsumer(
    actorSystem: ActorSystem,
    startAtOffset: Long,
    host: String,
    port: Int
)(implicit materializer: Materializer)
    extends ClickedConsumer(actorSystem, startAtOffset, host, port) {

  var metric: Map[Clicked, Int] = Map.empty

  def consume(record: ConsumerRecord[Array[Byte], String]): Future[Done] = {
    val clicked = Json.parse(record.value).as[Clicked]
    offset.set(record.offset)
    metric = metric + (clicked -> metric.get(clicked).map(_ + 1).getOrElse(1))
    println(metric)
    Future.successful(Done)
  }
}
