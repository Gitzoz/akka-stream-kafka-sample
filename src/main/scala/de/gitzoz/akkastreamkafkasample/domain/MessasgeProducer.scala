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

object MessageProducer {
  def settings(system: ActorSystem) = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")
}

class RandomClickProducer(actorSystem: ActorSystem, whatOptions: Seq[String], whereOptions: Seq[String])(implicit materializer: Materializer) {
  def randomWhat = whatOptions(Random.nextInt(whatOptions.length))
  def randomWhere = whereOptions(Random.nextInt(whereOptions.length))

  def publishClicksWithPlainSink(amount: Int) = Source(1 to amount)
    .map(_ => Clicked(randomWhat, randomWhere))
    .map(Json.toJson(_))
    .map(Json.stringify(_))
    .map { elem =>
      new ProducerRecord[Array[Byte], String]("clicked", elem)
    }.runWith(Producer.plainSink(MessageProducer.settings(actorSystem)))
}