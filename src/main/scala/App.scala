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

    getTaggedPhotos(tumblrApi, tag).map(getOriginalImages).map(photos => {

      println("Retrieved %d photos".format(photos.length))

      Random.shuffle(photos).headOption.map(photo => {

        println("Chose image at %s".format(photo.imgUrl))

        println("Glitching and then sending to Tumblr")

        (for {
          jsondata <- tumblrApi.post(
            "post",
            blogUrl,
            Map("type" -> "photo",
                "caption" -> createPostCaption(photo),
                "tags" -> "Cuttr, glitch, generative, random, %s".format(tag)),
            glitchImage(photo, glitchType)
          )
          response <- jsondata.decodeOption[TumblrResponse[PostId]]
        } yield response).map(response =>
          response.meta match {
            case Meta(201, msg) => {
              println("Post url:\n    http://%s/post/%d".format(blogUrl, response.response.id))
            }
            case Meta(status, msg) => {
              println("Status code %d was returned when creating post".format(status))
              println("    %s".format(msg))
            }
          }
        )

      })

    })

  }

  def glitchImage(photo: Photo, glitch: String): Array[Byte] = {

    println("Glitching image")

    Glitchr(
      ImageIO.read(
        new URL(photo.imgUrl)
      ),
      glitch
    )

  }

  def getTaggedPhotos(tumblr:TumblrAPI, tag: String): Option[List[PhotoPost]] = {
    for {
      stringdata <- tumblr.get("tagged", "", Map("tag" -> tag))
      jsondata <- stringdata.parseOption
      cursor = jsondata.cursor
      posts <- cursor.downField("response")
      postArray <- posts.focus.array
      filtered = filterPostType(postArray, "photo")
      output <- jArray(filtered).jdecode[List[PhotoPost]].toOption
    } yield output

  }

  def filterPostType(posts: JsonArray, `type`: String): JsonArray = {
    for {
      element <- posts
      typeField <- element.field("type")
      typeVal <- typeField.string
      if typeVal == `type`
    } yield element
  }

  case class Photo(imgUrl: String, blogName: String, postUrl: String, postDate: String)

  def getOriginalImages(photoposts: List[PhotoPost]): List[Photo] = {
    for {
      post <- photoposts
      photos = post.photos
      photo <- photos
      original = photo.original_size
    } yield Photo(original.url, post.blog_name, post.post_url, post.date)
  }


  def createPostCaption(photo: Photo) = {
    """
      <a href='${photo.imgUrl}'>Original</a> image courtesy of ${photo.blogName}
      <a href='${photo.postUrl}'>First posted</a> on ${photo.postDate}
    """
  }

}

