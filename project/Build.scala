import sbt._
import sbt.Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object ScalaGlitch extends Build {

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(

    organization := "com.rumblesan",

    scalaVersion := "2.11.6",

    version := "0.4"

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
  lazy val tumblrapi = "com.rumblesan.util" %% "tumblrapi" % "0.3.0"
  lazy val argonaut = "io.argonaut" %% "argonaut" % "6.0.4"
  lazy val config = "com.typesafe" % "config" % "1.2.0"
  lazy val scalaz = "org.scalaz" %% "scalaz-core" % "7.0.6"
  lazy val scodec = "org.scodec" %% "scodec-core" % "1.7.1"
  lazy val scodecbits = "org.scodec" %% "scodec-bits" % "1.0.6"
  lazy val scopt = "com.github.scopt" %% "scopt" % "3.3.0"

  lazy val scalacSettings = Seq("-feature", "-language:_", "-deprecation")

  lazy val sonnatypeRepo = Resolver.sonatypeRepo("public")
  lazy val defaultSettings = Defaults.defaultSettings ++ buildSettings ++ Seq(
    scalacOptions ++= scalacSettings
  )

  lazy val cuttr = Project(
    id = "cuttr",
    base = file("."),

    settings = defaultSettings ++ assemblySettings ++ Seq(
      libraryDependencies += config,
      libraryDependencies += tumblrapi,
      libraryDependencies += argonaut,
      libraryDependencies += scopt,
      resolvers += sonnatypeRepo,
      jarName in assembly := "cuttr.jar",
      target in assembly := file("./assembled")
    ) ++ mergingStrategy

  ) dependsOn(scalaglitch)

  lazy val scalaglitch = Project(
    id = "scalaglitch",
    base = file("scala-glitch"),
    settings = defaultSettings ++ Seq(
      libraryDependencies += scodec,
      libraryDependencies += scodecbits,
      libraryDependencies += scalaz
    )
  )

}
