organization := "com.ticketfly"

name := "test-play25"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.h2database" % "h2" % "1.4.192",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF")
