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

package de.gitzoz.akkastreamkafkasample

import scala.io.StdIn

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.gitzoz.akkastreamkafkasample.domain.CalculateClickedMetricsConsumer
import de.gitzoz.akkastreamkafkasample.domain.PrintClickedConsumer
import de.gitzoz.akkastreamkafkasample.domain.RandomClickProducer
import scala.concurrent.duration.DurationInt
import scala.concurrent.Await

object SampleApp {
  def main(args: Array[String]): Unit = {
    implicit val system       = ActorSystem("sample-app-system")
    implicit val materializer = ActorMaterializer()

    val whatOptions  = Seq("a", "b", "c", "d", "e", "f", "g", "h")
    val whereOptions = Seq("1", "2", "3", "4", "5", "6", "7", "8")

    val host  = "localhost"
    val port  = 9092
    val topic = "clicked"

    val producer =
      new RandomClickProducer(system, whatOptions, whereOptions, host, port)
    val printConsumer =
      new PrintClickedConsumer(system, startAtOffset = 0, host, port)
        .runConsumer(topic)
    val calculateConsumer =
      new CalculateClickedMetricsConsumer(system,
                                          startAtOffset = 0,
                                          host,
                                          port).runConsumer(topic)

    var run = true
    while (run) {
      println("Type 'add' to produce 10 Clicked")
      println("Enter exit to stop >>")
      val input = StdIn.readLine()
      if (input == "stop") {
        Await.result(system.terminate, 5.seconds)
        run = false
      } else if (input == "add") {
        producer.publishClicksWithPlainSink(10, topic)
      }

    }
  }
}
