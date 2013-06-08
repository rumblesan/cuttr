package com.rumblesan.cuttr

import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import java.io.{File, IOException}
import java.net.URL

import com.rumblesan.scalaglitch.Cuttr

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
            glitchImage(photo)
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

  def glitchImage(photo: Photo): Array[Byte] = {
    val image = ImageIO.read(new URL(photo.imgUrl))

    println("Glitching image")
    val glitcher = new Cuttr(image)

    val glitchedImage = glitchType match {
      case "smear" => glitcher.smear()
      case "cubist" => glitcher.cubist()
      case _ => glitcher.smear()
    }

    val baos = new ByteArrayOutputStream()
    ImageIO.write(glitchedImage, "jpg", baos)
    baos.flush()
    val imageData = baos.toByteArray()
    baos.close()
    imageData
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

  val descText = """
                   <a href='%s'>Original</a> image courtesy of %s
                   <a href='%s'>First posted</a> on %s
                 """

  def createPostCaption(photo: Photo) = {
    descText.format(
      photo.imgUrl,
      photo.blogName,
      photo.postUrl,
      photo.postDate
    )
  }



}

