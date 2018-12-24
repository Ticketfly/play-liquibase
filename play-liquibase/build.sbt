organization := "com.ticketfly"

name := "play-liquibase"

version := "2.0"

homepage := Some(url("https://github.com/Ticketfly/play-liquibase"))

organizationName := "Ticketfly, Inc."

organizationHomepage := Some(url("http://www.ticketfly.com"))

description := "Play Framework module for performing Liquibase schema migrations on application startup"

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.11.12", "2.12.8")

publishMavenStyle := true

val playVersion =  "2.6.5"

libraryDependencies ++= Seq(
  "org.liquibase"     % "liquibase-core"  % "3.6.2",
  "com.mattbertolini" % "liquibase-slf4j" % "2.0.0",
  "com.typesafe.play" %% "play"           % playVersion % Provided,
  "javax.inject"      % "javax.inject"    % "1"         % Provided
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-inaccessible",
  "-Xfuture"
)

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

pomExtra in Global := {
  <scm>
    <connection>scm:git:github.com:Ticketfly/play-liquibase.git</connection>
    <url>git@github.com:Ticketfly/play-liquibase.git</url>
  </scm>
  <developers>
    <developer>
      <id>dragisak</id>
      <name>Dragisa Krsmanovic</name>
      <url>https://github.com/dragisak/</url>
    </developer>
  </developers>
}
