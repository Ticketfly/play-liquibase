organization := "com.ticketfly"

name := "root"

scalaVersion := "2.12.14"

lazy val playLiquibaseRoot = project.in(file("."))
    .aggregate(playLiquibase, testPlay28)


lazy val playLiquibase = project.in(file("play-liquibase"))
    .enablePlugins(SbtPgp, Sonatype)

lazy val testPlay28 = project.in(file("test-play28"))
    .enablePlugins(PlayScala)
    .dependsOn(playLiquibase)
