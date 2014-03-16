package com.rumblesan.cuttr

import javax.imageio.ImageIO
import java.io.{File, IOException}
import java.net.URL

import com.rumblesan.scalaglitch.Glitchr

import com.rumblesan.util.tumblrapi.TumblrAPI

import scala.io.Source

import scala.util.Random

import com.typesafe.config._

import argonaut._, Argonaut._
import Json.JsonArray

import com.rumblesan.cuttr.tumblr._
import com.rumblesan.cuttr.models._



object App {

  lazy val config = ConfigFactory.load()

  lazy val tumblrApi = new TumblrAPI(
    config.getString("cuttr.oauth.apiKey"),
    config.getString("cuttr.oauth.apiSecret"),
    config.getString("cuttr.oauth.oauthToken"),
    config.getString("cuttr.oauth.oauthSecret")
  )

  lazy val blogUrl = config.getString("cuttr.blog.url")

  lazy val tag = config.getString("cuttr.search.tag")

  lazy val glitchType = config.getString("cuttr.glitch.type")

  def main(args: Array[String]) {

    println("###########\n#  Cuttr  #\n###########")

    println("Updating %s with photos from tag %s".format(blogUrl, tag))

    Tumblr.getTaggedPhotos(tumblrApi, tag).map(getOriginalImages).map(photos => {

      println("Retrieved %d photos".format(photos.length))

      Random.shuffle(photos).headOption.map(photo => {

        println("Chose image at %s".format(photo.imgUrl))

        println("Glitching and then sending to Tumblr")

        for {
          jsondata <- postToTumblr(photo, blogUrl, tag, glitchType)
          response <- jsondata.decodeOption[TumblrResponse[PostId]]
          _ = checkResponse(response, blogUrl)
        } yield response

      })

    })

  }

  def postToTumblr(glitchedPhoto: CuttrPhoto, blog: String, searchTag: String, glitch: String): Option[String] = {
    tumblrApi.post(
      "post",
      blog,
      Map(
        "type" -> "photo",
        "caption" -> glitchedPhoto.caption,
        "tags" -> "Cuttr, glitch, generative, random, %s".format(searchTag)
      ),
      glitchImage(glitchedPhoto, glitch)
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

  def glitchImage(photo: CuttrPhoto, glitch: String): Array[Byte] = {

    println("Glitching image")

    Glitchr(
      ImageIO.read(
        new URL(photo.imgUrl)
      ),
      glitch
    )

  }

  def getOriginalImages(photoposts: List[PhotoPost]): List[CuttrPhoto] = {
    for {
      post <- photoposts
      photos = post.photos
      photo <- photos
      original = photo.original_size
    } yield CuttrPhoto(original.url, post.blog_name, post.post_url, post.date)
  }

}

