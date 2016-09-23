import sbt._

// format: off

object Version {
  final val Scala     = "2.11.8"
  final val ScalaTest = "3.0.0"
  final val Akka	  = "2.4.10"
  final val AkkaStreamsKafka = "0.12"
  final val PlayFramework = "2.5.8"
}

object Library {
  val scalaTest = "org.scalatest" %% "scalatest" % Version.ScalaTest
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % Version.Akka
  val akkaStreams = "com.typesafe.akka" %% "akka-stream" % Version.Akka
  val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % Version.Akka
  val akkaStreamsTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % Version.Akka
  val akkaStreamsKafka = "com.typesafe.akka" %% "akka-stream-kafka" % Version.AkkaStreamsKafka
  val playJson   = "com.typesafe.play" %% "play-json" % Version.PlayFramework
}
