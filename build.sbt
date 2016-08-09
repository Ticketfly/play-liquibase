organization := "com.ticketfly"

name := "root"

scalaVersion := "2.11.8"

lazy val root = project.in(file("."))

lazy val playLiquibase = project.in(file("play-liquibase"))

lazy val testPlay24 = project.in(file("test-play24"))

lazy val testPlay25 = project.in(file("test-play25"))
