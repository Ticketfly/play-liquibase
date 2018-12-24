organization := "com.ticketfly"

name := "root"

scalaVersion := "2.11.12"

lazy val playLiquibaseRoot = project.in(file("."))
    .aggregate(playLiquibase, testPlay26)


lazy val playLiquibase = project.in(file("play-liquibase"))
    .enablePlugins(SbtPgp, Sonatype)

lazy val testPlay26 = project.in(file("test-play26"))
    .enablePlugins(PlayScala)
    .dependsOn(playLiquibase)
