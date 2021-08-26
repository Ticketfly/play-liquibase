organization := "com.ticketfly"

name := "play-liquibase"

version := "2.3-SNAPSHOT"

homepage := Some(url("https://github.com/Ticketfly/play-liquibase"))

organizationName := "Ticketfly, Inc."

organizationHomepage := Some(url("http://www.ticketfly.com"))

description := "Play Framework module for performing Liquibase schema migrations on application startup"

scalaVersion := "2.12.14"

crossScalaVersions := Seq("2.12.14", "2.13.6")

val playVersion = "2.8.7"

libraryDependencies ++= Seq(
  "org.liquibase" % "liquibase-core" % "3.6.2",
  "com.mattbertolini" % "liquibase-slf4j" % "2.0.0",
  "com.typesafe.play" %% "play-jdbc" % playVersion % Provided,
  "com.typesafe.play" %% "play" % playVersion % Provided,
  "javax.inject" % "javax.inject" % "1" % Provided
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)

scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n <= 12 => Seq("-Yno-adapted-args", "-Ywarn-inaccessible", "-Xfuture")
    case _ => Seq("-Ymacro-annotations")
  }
}

publishMavenStyle := true

publishTo := sonatypePublishTo.value

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(
  GitHubHosting("Ticketfly", "play-liquibase", "dragisak@gmail.com")
)

developers := List(
  Developer(
    id = "dragisak",
    name = "Dragisa Krsmanovic",
    email = "dragishak@gmail.com",
    url = url("https://github.com/dragisak")
  )
)
