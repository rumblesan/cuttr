package com.rumblesan.cuttr.util

import com.typesafe.config.{ Config, ConfigFactory }

import com.rumblesan.util.tumblrapi.TumblrAPI

import scopt.OptionParser

case class CuttrConfig(tumblrApi: TumblrAPI, blogUrl: String, searchTag: String, glitchType: String)

trait TumblrApiCreation {

  def createTumblrApi(apiKey: String, apiSecret: String, oauthToken: String, oauthSecret: String): TumblrAPI = {
    new TumblrAPI(
      apiKey,
      apiSecret,
      oauthToken,
      oauthSecret
    )
  }

}

trait CuttrConfigOps {

  def create(config: Config): CuttrConfig = {

    val tumblrApi = new TumblrAPI(
      config.getString("cuttr.oauth.apiKey"),
      config.getString("cuttr.oauth.apiSecret"),
      config.getString("cuttr.oauth.oauthToken"),
      config.getString("cuttr.oauth.oauthSecret")
    )

    val blogUrl = config.getString("cuttr.blog.url")

    val searchTag = config.getString("cuttr.search.tag")

    val glitchType = config.getString("cuttr.glitch.type")

    CuttrConfig(
      tumblrApi,
      blogUrl,
      searchTag,
      glitchType
    )

  }

}


object CuttrConfig extends CuttrConfigOps with TumblrApiCreation

object CuttrCliParser {

  val parser = new OptionParser[CuttrCliConfig]("cuttr") {
    head("cuttr", "0.4")
    opt[String]('f', "file") action { (x, c) =>
      c.copy(inputFile = Some(x)) } text("Input file to glitch")
    opt[String]('i', "id") action { (x, c) =>
      c.copy(inputTumblrPost = Some(x)) } text("Tumblr post id to glitch")
    opt[Unit]('r', "random") action { (_, c) =>
      c.copy(randomTumblr = true) } text("Glitch a random Tumblr post")
    opt[String]('o', "output") action { (x, c) =>
      c.copy(outputFile = Some(x)) } text("Write image to file")
    opt[Unit]('p', "post") action { (_, c) =>
      c.copy(postTumblr = true) } text("Post to Tumblr")
    opt[String]('t', "tag") action { (x, c) =>
      c.copy(searchTag = Some(x)) } text("Tag to search")
    opt[String]('b', "blog") action { (x, c) =>
      c.copy(blogUrl = Some(x)) } text("Blog to update")
    opt[String]('g', "glitch") action { (x, c) =>
      c.copy(glitch = x) } text("Glitch to use")
  }
}

case class CuttrCliConfig(
  inputFile: Option[String] = None,
  inputTumblrPost: Option[String] = None,
  randomTumblr: Boolean = false,
  outputFile: Option[String] = None,
  postTumblr: Boolean = false,
  searchTag: Option[String] = None,
  blogUrl: Option[String] = None,
  glitch: String = "cuttr"
)


