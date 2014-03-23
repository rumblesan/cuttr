import sbt._
import sbt.Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object ScalaGlitch extends Build {

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(

    organization := "com.rumblesan",

    scalaVersion := "2.10.3",

    version := "0.3"

  )

  lazy val mergingStrategy = Seq(
    mergeStrategy in assembly <<= (mergeStrategy in assembly) (
      (old) => {
        case "application.conf" => MergeStrategy.concat
        case x => old(x)
      }
    )
  )

  // Dependencies.
  lazy val specs = "org.specs2" %% "specs2" % "2.3.10" % "test"
  lazy val mockito = "org.mockito" % "mockito-core" % "1.9.5" % "test"
  lazy val tumblrapi = "com.rumblesan.util" %% "tumblrapi" % "0.2.0"
  lazy val argonaut = "io.argonaut" %% "argonaut" % "6.0.3"
  lazy val config = "com.typesafe" % "config" % "1.2.0"
  lazy val scalaz = "org.scalaz" %% "scalaz-core" % "7.0.6"

  lazy val defaultSettings = Defaults.defaultSettings ++ buildSettings ++ Seq(
    libraryDependencies += specs,
    libraryDependencies += mockito
  )

  lazy val cuttr = Project(
    id = "cuttr",
    base = file("."),

    settings = defaultSettings ++ assemblySettings ++ Seq(
      libraryDependencies += config,
      libraryDependencies += tumblrapi,
      libraryDependencies += argonaut,
      jarName in assembly := "cuttr.jar",
      target in assembly := file("./assembled")
    ) ++ mergingStrategy

  ).settings(
    scalacOptions ++= Seq("-feature", "-language:_", "-deprecation")
  ) dependsOn(scalaglitch)

  lazy val scalaglitch = Project(
    id = "scalaglitch",
    base = file("scala-glitch"),
    settings = defaultSettings ++ Seq(
      libraryDependencies += scalaz
    )
  ).settings(
    scalacOptions ++= Seq("-feature", "-language:_", "-deprecation")
  )

}
