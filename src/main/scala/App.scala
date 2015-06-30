package com.rumblesan.cuttr

import scala.io.Source
import scala.util.Random

import com.rumblesan.cuttr.util.{CuttrFileConfig, CuttrCliConfig, CuttrCliParser}
import com.rumblesan.cuttr.tumblr._
import com.rumblesan.cuttr.models._
import com.rumblesan.scalaglitch.Glitchr
import com.rumblesan.scalaglitch.types.{ImageCanvas, GlitchedImageData}
import com.rumblesan.scalaglitch.util.FileOps
import com.rumblesan.util.tumblrapi.TumblrAPI

import com.typesafe.config.ConfigFactory

import argonaut._, Argonaut._
import Json.JsonArray

import javax.imageio.ImageIO
import java.io.{File, IOException}
import java.net.URL


object App {

  lazy val config = CuttrFileConfig.create(
    ConfigFactory.load()
  )

  case class GlitchedPhotoPost(caption: String, imageData: GlitchedImageData)

  lazy val defaultSearchTag = "skyline"

  /**
   * cuttr.jar
   * [-f <filename> | --file <filename>]
   *     glitch an input file
   * [-i <tumblrPostId> | --id <tumblrPostId>]
   *     glitch a specific tumblr post
   *     e.g.    errrord.tumblr.com:122793635104
   * [-o <filename> | --output <filename>]
   *     write glitched image to a file
   * [-p | --post ]
   *     post the image to tumblr
   * [-r | --random]
   *     glitch a random tumblr post
   * [-t <tag> | --tag <tag>]
   *     tag to search for random image
   * [-g <glitchName> | --glitch <glitchName>]
   *     which glitch to use
   */
  def main(args: Array[String]) {
    println("###########\n#  Cuttr  #\n###########")
    System.exit(
      (for {
        cliConfig <- CuttrCliParser.parser.parse(args, CuttrCliConfig())
        exitCode <- run(cliConfig, config)
      } yield exitCode).getOrElse(1)
    )
  }

  def run(cliConfig: CuttrCliConfig, fileConfig: CuttrFileConfig): Option[Int] = {
    val searchTag = cliConfig.searchTag.getOrElse(defaultSearchTag)
    val glitchType = cliConfig.glitch
    val blogUrl = cliConfig.blogUrl.getOrElse(fileConfig.blogUrl)
    val tumblrApi = fileConfig.tumblrApi
    (
      (cliConfig.inputFile, cliConfig.inputTumblrPost, cliConfig.randomTumblr) match {
        case (Some(filePath), _, _) => glitchFile(filePath, glitchType)
        case (None, Some(postId), _) => glitchPost(tumblrApi, postId, glitchType)
        case (None, None, true) => glitchRandomPost(tumblrApi, searchTag, glitchType)
        case _ => {
          println("No input specified")
          None
        }
      }
    ).flatMap(
      imageInfo => (cliConfig.outputFile, cliConfig.postTumblr) match {
        case (Some(outputFile), _) => writeToFile(outputFile, imageInfo.imageData)
        case (None, true) => postToTumblr(tumblrApi, imageInfo.imageData, imageInfo.caption, blogUrl, searchTag, glitchType)
        case _ => {
          println("No output specified")
          None
        }
      }
    )
  }

  def glitchFile(inFile: String, glitchType: String): Option[GlitchedPhotoPost] = {
    Some(
      GlitchedPhotoPost(
        "Glitched input file",
        Glitchr(
          ImageCanvas(
            ImageIO.read(new File(inFile)),
            glitchType
          )
        )
      )
    )
  }

  def glitchPost(tumblrApi: TumblrAPI, tumblrPostId: String, glitchType: String): Option[GlitchedPhotoPost] = {
    for {
      post <- Tumblr.getSpecificPost(tumblrApi, tumblrPostId)
      _ = println(s"Retrieved post: $tumblrPostId")
      originalImages = getOriginalImages(List(post))
      photo <- Random.shuffle(originalImages).headOption
      _ = println(s"Glitching chosen image: ${photo.imgUrl}")
      photoData <- glitchImage(photo, glitchType)
    } yield GlitchedPhotoPost("glitched post", photoData)
  }

  def glitchRandomPost(tumblrApi: TumblrAPI, searchTag: String, glitchType: String): Option[GlitchedPhotoPost] = {
    for {
      tumblrPhotos <- Tumblr.getTaggedPhotos(tumblrApi, searchTag)
      _ = println(s"Retrieved ${tumblrPhotos.length} photos")
      originalImages = getOriginalImages(tumblrPhotos)
      _ = println(s"Found ${originalImages.length} images")
      photo <- Random.shuffle(originalImages).headOption
      _ = println(s"Glitching chosen image: ${photo.imgUrl}")
      photoData <- glitchImage(photo, glitchType)
    } yield GlitchedPhotoPost(photo.caption, photoData)
  }


  def writeToFile(outputPath: String, glitchedImage: GlitchedImageData): Option[Int] = {
    val outputFile = FileOps.glitchedImageToFile(outputPath, glitchedImage)
    println(s"Written to ${outputFile.getAbsolutePath()}")
    Some(0)
  }

  def postToTumblr(tumblrApi: TumblrAPI, glitchedImage: GlitchedImageData, caption: String, blogUrl: String, searchTag: String, glitchType: String): Option[Int] = {
    for {
      jsondata <- Tumblr.postToTumblr(tumblrApi, blogUrl, searchTag, glitchType, glitchedImage, caption)
      _ = println(s"Posting image to ${blogUrl}")
      response <- jsondata.decodeOption[TumblrResponse[PostId]]
      exitcode <- Tumblr.checkResponse(response, blogUrl)
    } yield exitcode
  }

  def getOriginalImages(photoposts: List[PhotoPost]): List[CuttrPhoto] = {
    for {
      post <- photoposts
      photos = post.photos
      photo <- photos
      original = photo.original_size
    } yield CuttrPhoto(original.url, post.blog_name, post.post_url, post.date)
  }

  def glitchImage(photo: CuttrPhoto, glitch: String): Option[GlitchedImageData] = {
    Some(
      Glitchr(
        ImageCanvas(
          ImageIO.read(new URL(photo.imgUrl)),
          glitch
        )
      )
    )
  }

}

