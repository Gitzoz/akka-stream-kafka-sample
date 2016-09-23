package de.gitzoz.akkastreamkafkasample.app

import akka.actor.ActorSystem
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class SampleApp {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("sample-app-system")
    
    Await.result(system.terminate(), 10.seconds)
  }
}