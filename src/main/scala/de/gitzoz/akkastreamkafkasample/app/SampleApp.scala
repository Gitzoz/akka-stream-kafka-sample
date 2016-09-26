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

import akka.actor.ActorSystem
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.stream.ActorMaterializer
import de.gitzoz.akkastreamkafkasample.domain.RandomClickProducer
import de.gitzoz.akkastreamkafkasample.domain.ClickedConsumer
import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn
import de.gitzoz.akkastreamkafkasample.domain.PrintClickedConsumer
import de.gitzoz.akkastreamkafkasample.domain.CalculateClickedMetricsConsumer

object SampleApp {
  def main(args: Array[String]): Unit = {
    implicit val system       = ActorSystem("sample-app-system")
    implicit val materializer = ActorMaterializer()

    val whatOptions  = Seq("a", "b", "c", "d", "e", "f", "g", "h")
    val whereOptions = Seq("1", "2", "3", "4", "5", "6", "7", "8")

    val producer      = new RandomClickProducer(system, whatOptions, whereOptions)
    val printConsumer = new PrintClickedConsumer(system, 0).runConsumer
    val calculateConsumer =
      new CalculateClickedMetricsConsumer(system, 0).runConsumer

    while (true) {
      println("Type 'add' to produce 10 Clicked")
      println("Enter exit to stop >>")
      val input = StdIn.readLine()
      if (input == "stop") {
        system.terminate()
        System.exit(0)
      } else if (input == "add") {
        producer.publishClicksWithPlainSink(10)
      }

    }
  }
}
