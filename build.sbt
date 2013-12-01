name := "twitter-prowl"

organization := "com.example"

version := "0.0.1"

scalaVersion := "2.9.3"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.9.2" % "test",
  "com.twitter" % "hbc-core" % "1.4.2",
  "com.twitter" % "hbc-twitter4j" % "1.4.2",
  "org.twitter4j" % "twitter4j-core" % "3.0.5",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "org.slf4j" % "slf4j-api" % "1.6.6",
  "org.slf4j" % "slf4j-simple" % "1.6.6",
  "org.json4s" %% "json4s-native" % "3.2.5"
)

initialCommands := "import io.github.joker1007.twitter_prowl._"

