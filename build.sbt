lazy val AkkaStreamKafkaSample = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin, GitVersioning)

libraryDependencies ++= Vector(
  Library.scalaTest % "test",
  Library.akkaActor,
  Library.akkaStreams,
  Library.akkaTestkit % "test",
  Library.akkaStreamsTestkit % "test",
  Library.akkaStreamsKafka
)

initialCommands := """|import de.gitzoz.akkastreamkafkasample._
                      |""".stripMargin
