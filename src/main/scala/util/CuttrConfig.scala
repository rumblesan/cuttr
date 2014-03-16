package com.rumblesan.cuttr.util

import com.typesafe.config.{ Config, ConfigFactory }

import com.rumblesan.util.tumblrapi.TumblrAPI

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

