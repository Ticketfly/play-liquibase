organization := "com.ticketfly"

name := "play-liquibase"

version := "1.5-SNAPSHOT"

homepage := Some(url("https://github.com/Ticketfly/play-liquibase"))

organizationName := "Ticketfly, Inc."

organizationHomepage := Some(url("http://www.ticketfly.com"))

description := "Play Framework module for performing Liquibase schema migrations on application startup"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.10.5", "2.11.8", "2.12.1")

def playVersion(scalaVersion: String) = CrossVersion.partialVersion(scalaVersion) match {
  case Some((2, scalaMajor)) if scalaMajor == 12 => "2.6.0-M1"
  case _                                         => "2.4.6"
}

libraryDependencies ++= Seq(
  "org.liquibase"     % "liquibase-core"  % "3.5.3",
  "com.mattbertolini" % "liquibase-slf4j" % "2.0.0",
  "com.typesafe.play" %% "play"           % playVersion(scalaVersion.value) % Provided,
  "javax.inject"      % "javax.inject"    % "1"                             % Provided
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
