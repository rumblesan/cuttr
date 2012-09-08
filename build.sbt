import AssemblyKeys._

name := "cuttr"

organization := "com.rumblesan"

version := "0.1.0"

scalaVersion := "2.9.1"

libraryDependencies := Seq(
  "org.specs2" %% "specs2" % "1.11" % "test",
  "com.github.scopt" %% "scopt" % "2.1.0",
  "org.scribe" % "scribe" % "1.3.2",
  "com.codahale" %% "jerkson" % "0.5.0"
)

resolvers += "repo.codahale.com" at "http://repo.codahale.com"

resolvers += "sonatype-public" at "https://oss.sonatype.org/content/groups/public"

assemblySettings

