package com.rumblesan.cuttr

import scala.io.Source
import scala.util.Random

import com.rumblesan.cuttr.util.CuttrConfig
import com.rumblesan.cuttr.tumblr._
import com.rumblesan.cuttr.models._
import com.rumblesan.scalaglitch.Glitchr
import com.rumblesan.scalaglitch.types.{ImageCanvas, GlitchedImageData}
import com.rumblesan.util.tumblrapi.TumblrAPI

import com.typesafe.config.ConfigFactory

import argonaut._, Argonaut._
import Json.JsonArray

import javax.imageio.ImageIO
import java.io.{File, IOException}
import java.net.URL


object App {

  lazy val config = CuttrConfig.create(
    ConfigFactory.load()
  )

  def main(args: Array[String]) {

    println("###########\n#  Cuttr  #\n###########")

    println("Updating %s with photos from tag %s".format(config.blogUrl, config.searchTag))

    val exitcode = for {
      tumblrPhotos <- Tumblr.getTaggedPhotos(config.tumblrApi, config.searchTag)
      _ = println(s"Retrieved ${tumblrPhotos.length} photos")
      originalImages <- getOriginalImages(tumblrPhotos)
      photo <- Random.shuffle(originalImages).headOption
      _ = println(s"Chosen image at ${photo.imgUrl}")
      _ = println("Glitching and then sending to Tumblr")
      photoData <- glitchImage(photo, config.glitchType)
      jsondata <- postToTumblr(config, photoData, photo.caption)
      response <- jsondata.decodeOption[TumblrResponse[PostId]]
      exitcode <- checkResponse(response, config.blogUrl)
    } yield exitcode

    System.exit(
      exitcode.getOrElse(1)
    )
  }

  def getOriginalImages(photoposts: List[PhotoPost]): Option[List[CuttrPhoto]] = {
    Some(
      for {
        post <- photoposts
        photos = post.photos
        photo <- photos
        original = photo.original_size
      } yield CuttrPhoto(original.url, post.blog_name, post.post_url, post.date)
    )
  }

  def glitchImage(photo: CuttrPhoto, glitch: String): Option[GlitchedImageData] = {
    val source = ImageCanvas(
      ImageIO.read(
        new URL(photo.imgUrl)
      ),
      glitch
    )

    Some(Glitchr(source))
  }

  def postToTumblr(config: CuttrConfig, photoData: GlitchedImageData, photoCaption: String): Option[String] = {
    config.tumblrApi.post(
      "post",
      config.blogUrl,
      Map(
        "type" -> "photo",
        "caption" -> photoCaption,
        "tags" -> "Cuttr, glitch, generative, random, %s".format(config.searchTag)
      ),
      photoData.data,
      photoData.extension
    )
  }

  def checkResponse(response: TumblrResponse[PostId], url: String): Option[Int] = {
    response.meta match {
      case Meta(201, msg) => {
        println("Post url:\n    http://%s/post/%d".format(url, response.response.id))
        Some(0)
      }
      case Meta(status, msg) => {
        println("Status code %d was returned when creating post".format(status))
        println("    %s".format(msg))
        None
      }
    }
  }

}

