organization := "com.ticketfly"

name := "root"

scalaVersion := "2.11.8"

lazy val playLiquibaseRoot = project.in(file("."))
    .aggregate(playLiquibase, testPlay25)


lazy val playLiquibase = project.in(file("play-liquibase"))
    .enablePlugins(SbtPgp, Sonatype)

lazy val testPlay25 = project.in(file("test-play25"))
    .enablePlugins(PlayScala)
    .dependsOn(playLiquibase)
