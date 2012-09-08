package com.rumblesan.cuttr

import scopt.immutable.OptionParser

object Arguments {

  val parser = new OptionParser[Config]("cuttr", "0.1.0") {

    def options = Seq(
      arg("<cfgfile>", "The config file with OAuth tokens") {
        (v:String, c:Config) => c.copy(cfgfile = v)
      },
      arg("<blogfile>", "The list of blogs to search") {
        (v:String, c:Config) => c.copy(blogfile = v)
      }
    )
  }

  def apply(args: Array[String]) = {
    parser.parse(args, Config())
  }
}

case class Config(cfgfile:String = "", blogfile:String = "")

