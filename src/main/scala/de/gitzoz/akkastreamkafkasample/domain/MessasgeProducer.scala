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

import scala.util.Random

import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.stream.scaladsl.Source
import de.gitzoz.akkastreamkafkasample.domain.MessagesOps.clickedWrites
import play.api.libs.json.Json
import org.apache.kafka.clients.producer.ProducerRecord
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.Materializer
import akka.stream.scaladsl.Sink

object MessageProducer {
  def settings(system: ActorSystem) =
    ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers("localhost:9092")
}

class RandomClickProducer(
    actorSystem: ActorSystem,
    whatOptions: Seq[String],
    whereOptions: Seq[String]
)(implicit materializer: Materializer) {
  def randomWhat  = whatOptions(Random.nextInt(whatOptions.length))
  def randomWhere = whereOptions(Random.nextInt(whereOptions.length))

  def publishClicksWithPlainSink(amount: Int) =
    Source(1 to amount)
      .map(_ => Clicked(randomWhat, randomWhere))
      .map(Json.toJson(_))
      .map(Json.stringify(_))
      .map { elem =>
        new ProducerRecord[Array[Byte], String]("clicked", elem)
      }
      .runWith(Producer.plainSink(MessageProducer.settings(actorSystem)))
}
