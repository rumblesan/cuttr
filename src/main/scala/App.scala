package com.rumblesan.cuttr

import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import java.io.{File, IOException}
import java.net.URL

import com.rumblesan.cuttr.glitch.Cuttr

import com.rumblesan.util.tumblrapi.TumblrAPI

import scala.io.Source

import scala.util.Random

import com.typesafe.config._

import argonaut._, Argonaut._
import Json.JsonArray

import tumblr._


object App {

  def main(args: Array[String]) {

    val config = ConfigFactory.load()

    println("###########\n#  Cuttr  #\n###########")

    //val blogSource = Source.fromFile(arguments.blogfile)
    //val blogList   = blogSource.getLines.toList
    //blogSource.close()

    val blogUrl = config.getString("blog.url")
    val tag = config.getString("search.tag")

    println("Updating %s with photos from tag %s".format(blogUrl, tag))

    val tumblrApi = new TumblrAPI(config.getString("oauth.apiKey"),
                                  config.getString("oauth.apiSecret"),
                                  config.getString("oauth.oauthToken"),
                                  config.getString("oauth.oauthSecret"))

    /*
    println("Setting up Tumblr OAuth")

    val allBlogInfo = getAllBlogInfo(tumblrApi, blogList)
    val allPhotos   = allBlogInfo.flatMap(getBlogPhotos(tumblrApi, _, tag))

    println("Got %d photos in total".format(allPhotos.length))
    val selection = Random.shuffle(allPhotos).headOption

    selection.map(
      imageInfo => {
        println("Retrieving image at url %s".format(imageInfo.imgUrl))
        val image = ImageIO.read(new URL(imageInfo.imgUrl))

        println("Glitching image")
        val glitcher = new Cuttr(image)
        val glitchedImage = glitcher.glitch()

        val baos = new ByteArrayOutputStream()
        ImageIO.write(glitchedImage, "jpg", baos)
        baos.flush()
        val imageData = baos.toByteArray()
        baos.close()

        val postCaption = createPostCaption(imageInfo)
        println(postCaption)

        val params = Map("type" -> "photo",
                         "caption" -> postCaption,
                         "tags" -> "Cuttr, glitch, generative, random")

        println("Sending image to Tumblr")
        tumblrApi.post("post", blogUrl, params, imageData).map(
          jsonString => {

            val jsonResponse = Json.parse[TumblrPostResponse](jsonString)

            jsonResponse.meta match {
              case Meta(201, msg) => {

                jsonResponse.response match {
                  case Some(postId) => {
                    println("Post url:\n    http://%s/post/%d".format(blogUrl, postId.id))
                  }
                  case None => println("Something went wrong")
                }

              }
              case Meta(status, msg) => {
                println("Status code %d was returned when creating post".format(status))
                println("    %s".format(msg))
                None
              }
            }


          }
        )
      }
    )
    */
  }

  def filterPostType(posts: JsonArray, `type`: String): JsonArray = {
    for {
      element <- posts
      typeField <- element.field("type")
      typeVal <- typeField.string
      if typeVal == `type`
    } yield element
  }

  def getTaggedPhotos(tumblr:TumblrAPI, tag: String): Option[TumblrResponse[PostQuery[PhotoPost]]] = {
    println("Getting photos for tag %s".format(tag))

    val defaultParams = Map("type" -> "photo")

    val photoPosts = for {
      stringdata <- tumblr.get("tagged")
      jsondata <- stringdata.parseOption
      cursor = jsondata.cursor
      posts <- cursor.downField("posts")
      postArray <- posts.focus.array
      filtered = filterPostType(postArray, tag)
      updated = posts.set(jArray(filtered)).undo
      output <- updated.jdecode[TumblrResponse[PostQuery[PhotoPost]]].toOption
    } yield output

    photoPosts

  }



  /*
  def createPostCaption(info:PhotoInfo) = {
    val description = "<a href='%s'>Original</a>" +
                      " image courtesy of " +
                      "<a href='%s'>%s</a>" +
                      "\n" +
                      "<a href='%s'> First posted</a> on %s"
    description.format(
      info.imgUrl,
      info.blogUrl,
      info.blogName,
      info.postUrl,
      info.postDate
    )
  }
  */



}

