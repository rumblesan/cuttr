package com.rumblesan.cuttr

import com.rumblesan.cuttr.tumblr._

import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import java.io.{File, IOException}

import com.rumblesan.cuttr.glitch.Cuttr

import com.rumblesan.tumblr.api._
import com.codahale.jerkson.Json

import scala.io.Source

object App {

  def main(args: Array[String]) {
    Arguments(args) map { config =>
      run(config)
    }
  }

  def getAllPhotos(tumblr:TumblrAPI, blogs:List[String], tag:String = "") = {

    // Fold across the blogs, getting the info from each one as a map
    blogs.foldLeft(List.empty[Map[String,String]])(
      (info, blogUrl) => {
        // This will fall over if the format isn't right
        // Need to make this more generic
        val apiResponse = Json.parse[APICall[Blog]](tumblr.get("info", blogUrl))
        if (apiResponse.meta.status == 200) {
          val blogInfo = apiResponse.response.blog
          Map("url" -> blogUrl, "name" -> blogInfo.url.toString, "updated" -> blogInfo.updated.toString) :: info
        } else {
          info
        }
      }
    )

  }

  def getBlogPhotos(tumblr:TumblrAPI, blogInfo:Map[String,String], tag:String = "") = {

    val defaultParams = Map("type" -> "photo")

    val params = if (tag.isEmpty) {
      defaultParams
    } else {
      defaultParams + (("tag", tag))
    }

    val jsonData = tumblr.get("posts", blogInfo("url"), defaultParams)
    val apiResponse = Json.parse[APICall[BlogPosts[PhotoPost]]](jsonData)

    val posts = apiResponse.response.posts

    posts.flatMap {
      post => {
        post.photos.headOption
      }
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

