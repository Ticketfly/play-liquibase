organization := "com.ticketfly"

name := "test-play28"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.14"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.h2database" % "h2" % "1.4.192",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oDF")
