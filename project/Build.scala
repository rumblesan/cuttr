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
  lazy val tumblrapi = "com.rumblesan.util" %% "tumblrapi" % "0.2.0"
  lazy val argonaut = "io.argonaut" %% "argonaut" % "6.0-RC1"  
  lazy val config = "com.typesafe" % "config" % "1.0.1"

  lazy val defaultSettings = Defaults.defaultSettings ++ buildSettings ++ Seq(
    libraryDependencies += specs,
    libraryDependencies += mockito
  )

  lazy val cuttr = Project(
    id = "cuttr",
    base = file("."),

    settings = buildSettings ++ Seq(
      libraryDependencies += config,
      libraryDependencies += tumblrapi,
      libraryDependencies += argonaut
    )

  ) dependsOn(scalaglitch)

  lazy val scalaglitch = Project(
    id = "scalaglitch",
    base = file("scala-glitch"),
    settings = defaultSettings
  ).settings(
    scalacOptions ++= Seq("-feature", "-language:_")
  )

}
