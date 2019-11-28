organization := "com.ticketfly"

name := "play-liquibase"

version := "2.2-SNAPSHOT"

homepage := Some(url("https://github.com/Ticketfly/play-liquibase"))

organizationName := "Ticketfly, Inc."

organizationHomepage := Some(url("http://www.ticketfly.com"))

description := "Play Framework module for performing Liquibase schema migrations on application startup"

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.11.12", "2.12.10")

val playVersion = "2.6.5"

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
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-inaccessible",
  "-Xfuture"
)

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
