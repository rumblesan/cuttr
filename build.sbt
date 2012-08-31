name := "cuttr"

organization := "com.rumblesan"

version := "0.1.0"

scalaVersion := "2.9.1"

libraryDependencies := Seq(
  "org.specs2" %% "specs2" % "1.11" % "test",
  "com.github.scopt" %% "scopt" % "2.1.0"
)

resolvers += "sonatype-public" at "https://oss.sonatype.org/content/groups/public"

