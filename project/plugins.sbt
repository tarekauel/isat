logLevel := Level.Warn

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.0")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

resolvers ++= Seq(
  "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)