package com.rumblesan.cuttr

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
        println("Getting info for %s".format(blogUrl))
        tumblr.get("info", blogUrl, Map("type" -> "info")).map(
          info => {
            info.asInstanceOf[InfoQuery].blog
          }
        )
      }
    )
  }

  def getBlogPhotos(tumblr:TumblrAPI, blogInfo:BlogInfo, tag:String = ""):List[PhotoInfo] = {

    println("Getting photos for %s".format(blogInfo.title))

    val defaultParams = Map("type" -> "photo")

    val params = if (tag.isEmpty) {
      defaultParams
    } else {
      defaultParams + (("tag", tag))
    }


    // The API doesn't handle the leading http:// or trailing slash
    // It bloody well should, fix it you git
    val urlRegex = """http://(.*)/""".r
    val urlRegex(cleanUrl) = blogInfo.url

    // Option[PhotoQuery]
    val blogPosts = tumblr.get("posts", cleanUrl, params).map(_.asInstanceOf[PhotoQuery])

    println(blogInfo.url)
    println(blogPosts)

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

    println("Connected to Tumblr")

    val tag = cfg("search")("tag")
    val allBlogInfo = getAllBlogInfo(tumblrApi, blogList)
    println(allBlogInfo)
    val allPhotos   = allBlogInfo.flatMap(getBlogPhotos(tumblrApi, _, tag))

    println("Got %d photos".format(allPhotos.length))
    val selection = Random.shuffle(allPhotos).headOption
    println(selection)

    selection.map(
      imageInfo => {
        val image = ImageIO.read(new URL(imageInfo.imgUrl))

        println("Getting it")

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

        val imgResponse = tumblrApi.post("post",
                                         blogUrl,
                                         params,
                                         imageData)
        println(imgResponse)

        ImageIO.write(glitchedImage, "jpg", new File("output.jpeg"))
      }
    )

  }

}

