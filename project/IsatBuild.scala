import play.sbt.PlayImport._
import play.sbt.PlayScala
import play.sbt.routes.RoutesKeys._
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin.autoImport._
import sbtassembly.MergeStrategy
import sbtassembly.PathList

object IsatBuild extends Build {

  lazy val commonSettings = Seq(
    version := "0.1-SNAPSHOT",
    organization := "isat",
    scalaVersion := "2.11.6",
    libraryDependencies ++= Seq(
      "com.typesafe.scala-logging" % "scala-logging_2.11" % "3.1.0",
      "org.slf4j" % "slf4j-log4j12" % "1.7.12"
    )

  )

  lazy val root = (project in file(".")).settings(commonSettings: _*)
    .aggregate(model, frontend, core, streamConsumer)

  lazy val model = (project in file("model")).settings(commonSettings: _*).settings({
    libraryDependencies ++= Seq(
      "org.twitter4j" % "twitter4j-core" % "4.0.3" ,
      "org.twitter4j" % "twitter4j" % "4.0.3" ,
      "com.google.code.gson" % "gson" % "2.3.1"
    )
  })

  lazy val frontend = (project in file("frontend")).dependsOn(model).dependsOn(core)
    .enablePlugins(PlayScala).settings(commonSettings: _*).settings(Seq(
      libraryDependencies ++= Seq(
        jdbc,
        cache,
        ws,
        specs2 % Test
      ),
      resolvers ++= Seq(
        "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
        "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"),

      // Play provides two styles of routers, one expects its actions to be injected, the
      // other, legacy style, accesses its actions statically.
      routesGenerator := InjectedRoutesGenerator
    ))


  lazy val streamConsumer = (project in file("streamConsumer")).dependsOn(model)
    .settings(commonSettings: _*).settings(Seq(
      libraryDependencies ++= Seq(
        "org.twitter4j" % "twitter4j-core" % "4.0.3" ,
        "org.twitter4j" % "twitter4j-stream" % "4.0.3" ,
        "org.twitter4j" % "twitter4j" % "4.0.3"
      ),
      assemblyMergeStrategy in assembly := {
        case PathList("org", "slf4j", xs @ _*) => MergeStrategy.first
        case x =>
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
      }
    ))



  lazy val core = (project in file("core")).dependsOn(model).settings(commonSettings: _*).settings(
      Seq(libraryDependencies ++= Seq(
          "org.twitter4j" % "twitter4j-core" % "4.0.3",
          "org.twitter4j" % "twitter4j-stream" % "4.0.3",
          "com.google.code.gson" % "gson" % "2.3.1",
          "org.apache.spark" % "spark-core_2.11" % "1.4.0" % "provided",
          "org.apache.spark" % "spark-graphx_2.11" % "1.4.0" % "provided",,
          "org.apache.spark" % "spark-sql_2.11" % "1.4.0" % "provided",,
          "org.apache.spark" % "spark-streaming_2.11" % "1.4.0" % "provided",,
          "javax.persistence" % "persistence-api" % "1.0.2"
        ),
        fork := true,
        baseDirectory in run := file("./"),
        javaOptions += "-Dlog4j.configuration=log4j.properties"
      ))
}