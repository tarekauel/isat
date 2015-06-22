name := "ISAT"

version := "1.0"

scalaVersion in ThisBuild := "2.11.6"

test in assembly := {}

javacOptions in ThisBuild ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions in ThisBuild += "-target:jvm-1.7"