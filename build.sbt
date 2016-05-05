organization := "com.ticketfly"

name := "play-liquibase"

version := "1.1-SNAPSHOT"

homepage := Some(url("https://github.com/Ticketfly/play-liquibase"))

organizationName := "Ticketfly, Inc."

organizationHomepage := Some(url("http://www.ticketfly.com"))

description := "Play Framework module for performing Liquibase schema migrations on application startup"

scalaVersion := "2.11.8"

crossScalaVersions := Seq("2.10.5", "2.11.8")

libraryDependencies ++= Seq(
  "org.liquibase"     % "liquibase-core"  % "3.5.0",
  "com.mattbertolini" % "liquibase-slf4j" % "2.0.0",
  "com.typesafe.play" %% "play"           % "2.4.6" % Provided,
  "javax.inject"      % "javax.inject"    % "1"     % Provided
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
