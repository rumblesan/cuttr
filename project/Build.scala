import sbt._
import sbt.Keys._

object ScalaGlitch extends Build {

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(

    organization := "com.rumblesan",

    scalaVersion := "2.10.1",

    version := "0.2"

  )

  // Dependencies.
  lazy val specs = "org.specs2" %% "specs2" % "1.14" % "test"
  lazy val mockito = "org.mockito" % "mockito-core" % "1.8.5" % "test"
  lazy val scopt = "com.github.scopt" %% "scopt" % "2.1.0"
  lazy val jumblr = "com.tumblr" % "jumblr" % "0.0.6"

  lazy val defaultSettings = Defaults.defaultSettings ++ buildSettings ++ Seq(
    libraryDependencies += specs,
    libraryDependencies += mockito
  )

  lazy val cuttr = Project(
    id = "cuttr",
    base = file("."),

    settings = buildSettings ++ Seq(
      libraryDependencies += scopt,
      libraryDependencies += jumblr
    )

  ) aggregate(scalaglitch)

  lazy val scalaglitch = Project(
    id = "scala-glitch",
    base = file("scala-glitch"),
    settings = defaultSettings
  )

}
