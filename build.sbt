import AssemblyKeys._

name := "cuttr"

organization := "com.rumblesan"

version := "0.1.1"

scalaVersion := "2.9.1"

libraryDependencies := Seq(
  "org.specs2" %% "specs2" % "1.11" % "test",
  "com.github.scopt" %% "scopt" % "2.1.0",
  "org.scribe" % "scribe" % "1.3.2",
  "com.codahale" %% "jerkson" % "0.5.0",
  "com.rumblesan" %% "tumblr.api" % "0.2.0" from "http://cloud.github.com/downloads/rumblesan/tumblr_scala_api/tumblr-scala-api_2.9.1-0.2.0.jar"
)

resolvers += "repo.codahale.com" at "http://repo.codahale.com"

resolvers += "sonatype-public" at "https://oss.sonatype.org/content/groups/public"

assemblySettings

