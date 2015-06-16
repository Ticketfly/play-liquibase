organization := "com.ticketfly"

name := "play-liquibase"

version := "0.1"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.10.5", "2.11.6")

libraryDependencies ++= Seq(
  "org.liquibase"     % "liquibase-core"  % "3.3.3",
  "com.mattbertolini" % "liquibase-slf4j" % "1.2.1",
  "com.typesafe.play" %% "play"           % "2.4.0" % Provided,
  "javax.inject"      % "javax.inject"    % "1"     % Provided
)

val tflyArtifactory = "http://build.ticketfly.com/artifactory/"

publishTo := {
  if (isSnapshot.value)
    Some("tfly-snaphot" at tflyArtifactory + "libs-snapshot-local")
  else
    Some("tfly-release" at tflyArtifactory + "libs-release-local")
}

credentials += Credentials(Path.userHome / ".artifactory" / ".credentials")
