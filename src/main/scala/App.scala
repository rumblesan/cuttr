package com.rumblesan.cuttr

import javax.imageio.ImageIO
import java.io.{File, IOException}
import java.net.URL

import com.rumblesan.scalaglitch.Glitchr

import com.rumblesan.util.tumblrapi.TumblrAPI

import com.rumblesan.cuttr.util.CuttrConfig

import scala.io.Source

import scala.util.Random

import com.typesafe.config._

import argonaut._, Argonaut._
import Json.JsonArray

import com.rumblesan.cuttr.tumblr._
import com.rumblesan.cuttr.models._



object App {

  lazy val config = CuttrConfig.create(
    ConfigFactory.load()
  )

  def main(args: Array[String]) {

    println("###########\n#  Cuttr  #\n###########")

    println("Updating %s with photos from tag %s".format(config.blogUrl, config.searchTag))

    for {
      tumblrPhotos <- Tumblr.getTaggedPhotos(config.tumblrApi, config.searchTag)
      _ = println(s"Retrieved ${tumblrPhotos.length} photos")
      originalImages = getOriginalImages(tumblrPhotos)
      photo <- Random.shuffle(originalImages).headOption
      _ = println(s"Chosen image at ${photo.imgUrl}")
      _ = println("Glitching and then sending to Tumblr")
      photoData = glitchImage(photo, config.glitchType)
      jsondata <- postToTumblr(config, photoData, photo.caption)
      response <- jsondata.decodeOption[TumblrResponse[PostId]]
      _ = checkResponse(response, config.blogUrl)
    } yield response

  }

  def getOriginalImages(photoposts: List[PhotoPost]): List[CuttrPhoto] = {
    for {
      post <- photoposts
      photos = post.photos
      photo <- photos
      original = photo.original_size
    } yield CuttrPhoto(original.url, post.blog_name, post.post_url, post.date)
  }

  def glitchImage(photo: CuttrPhoto, glitch: String): Array[Byte] = {
    Glitchr(
      ImageIO.read(
        new URL(photo.imgUrl)
      ),
      glitch
    )
  }

  def postToTumblr(config: CuttrConfig, photoData: Array[Byte], photoCaption: String): Option[String] = {
    config.tumblrApi.post(
      "post",
      config.blogUrl,
      Map(
        "type" -> "photo",
        "caption" -> photoCaption,
        "tags" -> "Cuttr, glitch, generative, random, %s".format(config.searchTag)
      ),
      photoData
    )
  }

  def checkResponse(response: TumblrResponse[PostId], url: String): Unit = {
    response.meta match {
      case Meta(201, msg) => {
        println("Post url:\n    http://%s/post/%d".format(url, response.response.id))
      }
      case Meta(status, msg) => {
        println("Status code %d was returned when creating post".format(status))
        println("    %s".format(msg))
      }
    }
  }

}

