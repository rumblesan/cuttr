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

case class PhotoInfo(blogName:String,
                     blogUrl:String,
                     postUrl:String,
                     postDate:String,
                     imgUrl:String)

object App {

  def main(args: Array[String]) {
    Arguments(args) map { config =>
      run(config)
    }
  }

  def getAllBlogInfo(tumblr:TumblrAPI, blogs:List[String]):List[BlogInfo] = {
    // flatMap across the blogs, getting the info from each one
    blogs.flatMap(
      blogUrl => {
        tumblr.get("info", blogUrl).map(
          info => {
            info.asInstanceOf[InfoQuery].blog
          }
        )
      }
    )
  }

  def getBlogPhotos(tumblr:TumblrAPI, blogInfo:BlogInfo, tag:String = ""):List[PhotoInfo] = {

    val defaultParams = Map("type" -> "photo")

    val params = if (tag.isEmpty) {
      defaultParams
    } else {
      defaultParams + (("tag", tag))
    }

    // Option[PhotoQuery]
    val blogPosts = tumblr.get("posts", blogInfo.url, defaultParams).map(_.asInstanceOf[PhotoQuery])

    blogPosts.map(
      query => {
        query.posts.map(
          post => {
            val blogName = blogInfo.name
            val blogUrl  = blogInfo.url
            val postUrl  = post.post_url
            val postDate = post.date
            val photo    = post.photos.head
            val photoSize = photo.alt_sizes.head
            PhotoInfo(blogName, blogUrl, postUrl, postDate, photoSize.url)
          }
        )
      }
    ).getOrElse(List.empty[PhotoInfo])

  }


  def run(config:Config) {

    val cfgSource = Source.fromFile(config.cfgfile)
    val cfgJson   = cfgSource.mkString
    cfgSource.close()

    val cfg = Json.parse[Map[String,Map[String,String]]](cfgJson)

    val blogSource = Source.fromFile(config.blogfile)
    val blogList   = blogSource.getLines.toList
    blogSource.close()

    val blogUrl = cfg("blog")("url")

    val oauthCfg = cfg("oauth")

    val tumblrApi = new TumblrAPI(oauthCfg("apiKey"),
                                  oauthCfg("apiSecret"),
                                  oauthCfg("oauthToken"),
                                  oauthCfg("oauthSecret"))

    val tag = cfg("search")("tag")
    val allBlogInfo = getAllBlogInfo(tumblrApi, blogList)
    val allPhotos   = allBlogInfo.flatMap(getBlogPhotos(tumblrApi, _, tag))

    val selection = Random.shuffle(allPhotos).headOption

    val imageFile = selection.map(
      imageInfo => {
        ImageIO.read(new URL(imageInfo.imgUrl))
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
                                       blogUrl,
                                       params,
                                       imageData)
      println(imgResponse)

     ImageIO.write(glitchedImage, "jpg", new File("output.jpeg"))

    }

  }

}

