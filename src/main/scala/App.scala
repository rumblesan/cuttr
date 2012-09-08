package com.rumblesan.cuttr

import com.rumblesan.cuttr.tumblr._

import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import java.io.{File, IOException}
import java.net.URL

import com.rumblesan.cuttr.glitch.Cuttr

import com.rumblesan.tumblr.api._
import com.codahale.jerkson.Json

import scala.io.Source

import scala.util.Random

object App {

  def main(args: Array[String]) {
    Arguments(args) map { config =>
      run(config)
    }
  }

  def getAllPhotos(tumblr:TumblrAPI, blogs:List[String], tag:String = "") = {

    // Fold across the blogs, getting the info from each one as a map
    val blogInfo = blogs.foldLeft(List.empty[Map[String,String]])(
      (info, blogUrl) => {
        // This will fall over if the format isn't right
        // Need to make this more generic
        val apiResponse = Json.parse[BlogInfoAPICall](tumblr.get("info", blogUrl))
        if (apiResponse.meta.status == 200) {
          val blogInfo = apiResponse.response.blog
          Map("url" -> blogUrl, "name" -> blogInfo.url.toString, "updated" -> blogInfo.updated.toString) :: info
        } else {
          info
        }
      }
    )

    blogInfo.flatMap(
      blog => {
        getBlogPhotos(tumblr, blog, tag)
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
    val apiResponse = Json.parse[BlogPhotoAPICall](jsonData)

    val posts = apiResponse.response.posts

    // TODO
    // turn this into a for comprehension
    posts.flatMap {
      post => {
        post.photos.headOption.flatMap{_.alt_sizes.headOption}
      }
    }

  }


  def run(config:Config) {

    val cfgSource = Source.fromFile(config.cfgfile)
    val cfgJson   = cfgSource.mkString
    cfgSource.close()

    val cfg = Json.parse[Map[String,String]](cfgJson)

    val blogSource = Source.fromFile(config.blogfile)
    val blogList   = blogSource.getLines.toList
    blogSource.close()

    val tumblrApi = new TumblrAPI(cfg("apiKey"),
                                  cfg("apiSecret"),
                                  cfg("oauthToken"),
                                  cfg("oauthSecret"))

    val photos = getAllPhotos(tumblrApi, blogList, "landscape")

    val selection = Random.shuffle(photos).headOption

    val imageFile = selection.map(
      imageInfo => {
        ImageIO.read(new URL(imageInfo.url))
      }
    )

    imageFile map { image =>

      val glitcher = new Cuttr(image)
      val glitchedImage = glitcher.glitch()

      val baos = new ByteArrayOutputStream()
      ImageIO.write(glitchedImage, "jpg", baos)
      baos.flush()
      val imageData = baos.toByteArray()
      baos.close()

      val params = Map("type" -> "photo",
                       "caption" -> "Haha, looks like this is all coming together!",
                       "tags" -> "testing, glitch, generative, random")

      val imgResponse = tumblrApi.post("post",
                                       "rumblesan.tumblr.com",
                                       params,
                                       imageData)
      println(imgResponse)

     ImageIO.write(glitchedImage, "jpg", new File("output.jpeg"))

    }

  }

}

