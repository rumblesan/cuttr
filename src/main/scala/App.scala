package com.rumblesan.cuttr


import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import java.io.{File, IOException}

import com.rumblesan.cuttr.glitch.Cuttr

import com.rumblesan.tumblr.api._
import com.codahale.jerkson.Json._

object App {

  def main(args: Array[String]) {
    Arguments(args) map { config =>
      run(config)
    }
  }


  def run(config:Config) {

    val inputfile = try {
      Some(ImageIO.read(new File(config.inputfile)))
    } catch {
      case ioe: IOException => {
        None
      }
    }

    inputfile map { image =>

      val source = scala.io.Source.fromFile(config.cfgfile)
      val lines = source.mkString
      source.close()

      val cfg = parse[Map[String,String]](lines)

      val glitcher = new Cuttr(image)
      val glitchedImage = glitcher.glitch()

      val baos = new ByteArrayOutputStream()
      ImageIO.write(glitchedImage, "jpg", baos)
      baos.flush()
      val imageData = baos.toByteArray()
      baos.close()

      val tumblrApi = new TumblrAPI(cfg("apiKey"),
                                    cfg("apiSecret"),
                                    cfg("oauthToken"),
                                    cfg("oauthSecret"))
      val params = Map("type" -> "photo",
                       "caption" -> "Testing out Cuttr, a glitching program I'm developing",
                       "tags" -> "testing, glitch, generative, random")
      /*
      val imgResponse = tumblrApi.post("post",
                                       "rumblesan.tumblr.com",
                                       params,
                                       imageData)
      println(imgResponse)
      */

     ImageIO.write(glitchedImage, "jpg", new File("output.jpeg"))

    }

  }

}

