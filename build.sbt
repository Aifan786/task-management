ThisBuild / version := "0.1.0-SNAPSHOT"

name := "task-management"
maintainer := "aifanul@terra.do"

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "mysql" % "mysql-connector-java" % "8.0.29",
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

PlayKeys.playDefaultPort := 9000
