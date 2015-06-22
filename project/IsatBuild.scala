import play.sbt.PlayImport._
import play.sbt.PlayScala
import play.sbt.routes.RoutesKeys._
import sbt.Keys._
import sbt._

object IsatBuild extends Build {

  lazy val root = (project in file(".")).
    aggregate(model, frontend, core, streamConsumer)

  lazy val model = project in file("model") settings {
    libraryDependencies ++= Seq(
      "org.twitter4j" % "twitter4j-core" % "4.0.3" % "provided",
      "org.twitter4j" % "twitter4j" % "4.0.3" % "provided",
//      "org.apache.spark" % "spark-sql_2.11" % "1.4.0",
      "com.google.code.gson" % "gson" % "2.3.1" % "provided",
      "org.slf4j" % "slf4j-api" % "1.7.12" % "provided"
    )
  }

  lazy val frontend = (project in file("frontend"))dependsOn model dependsOn core enablePlugins PlayScala settings(
    libraryDependencies ++= Seq(
      jdbc,
      cache,
      ws,
      specs2 % Test
    ),
    resolvers ++= Seq(
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/" ),

    // Play provides two styles of routers, one expects its actions to be injected, the
    // other, legacy style, accesses its actions statically.
    routesGenerator := InjectedRoutesGenerator
  )


  lazy val streamConsumer = (project in file("streamConsumer")) dependsOn model settings {
    libraryDependencies ++= Seq(
      "org.twitter4j" % "twitter4j-core" % "4.0.3" % "provided",
      "org.twitter4j" % "twitter4j-stream" % "4.0.3" % "provided",
      "org.twitter4j" % "twitter4j" % "4.0.3" % "provided",
      "com.typesafe.scala-logging" % "scala-logging_2.11" % "3.1.0" % "provided",
      "org.slf4j" % "slf4j-log4j12" % "1.2" % "provided"
    )
  }

  lazy val core = (project in file("core")) dependsOn model settings (
      libraryDependencies ++= Seq(
        "org.twitter4j" % "twitter4j-core" % "4.0.3",
        "org.twitter4j" % "twitter4j-stream" % "4.0.3",
        "com.google.code.gson" % "gson" % "2.3.1",
        "org.apache.spark" % "spark-core_2.11" % "1.4.0",
        "org.apache.spark" % "spark-graphx_2.11" % "1.4.0",
        "org.apache.spark" % "spark-sql_2.11" % "1.4.0",
        "org.apache.spark" % "spark-streaming_2.11" % "1.4.0",
        "javax.persistence" % "persistence-api" % "1.0.2"
      )
    ) settings(
      fork := true
    )
}