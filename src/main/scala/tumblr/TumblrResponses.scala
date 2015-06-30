package com.rumblesan.cuttr.tumblr

import argonaut._, Argonaut._

import com.rumblesan.util.tumblrapi.TumblrAPI
import com.rumblesan.scalaglitch.types.GlitchedImageData


object Tumblr {

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

  def getSpecificPost(tumblr: TumblrAPI, tumblrPostId: String): Option[PhotoPost] = {
    val blogPostId = tumblrPostId.split(":").toList
    for {
      blogUrl <- blogPostId.headOption
      postId <- blogPostId.tail.headOption
      stringdata <- tumblr.get("posts", blogUrl, Map("id" -> postId))
      jsondata <- stringdata.parseOption
      cursor = jsondata.cursor
      responseCursor <- cursor.downField("response")
      postsCursor <- responseCursor.downField("posts")
      postArray <- postsCursor.focus.array
      filtered = filterPostType(postArray, "photo")
      output <- jArray(filtered).jdecode[List[PhotoPost]].toOption
      post <- output.headOption
    } yield post
  }

  def filterPostType(posts: JsonArray, `type`: String): JsonArray = {
    for {
      element <- posts
      typeField <- element.field("type")
      typeVal <- typeField.string
      if typeVal == `type`
    } yield element
  }

  def postToTumblr(tumblr: TumblrAPI, blogUrl: String, searchTag: String, glitchType: String, photoData: GlitchedImageData, photoCaption: String): Option[String] = {
    tumblr.post(
      "post",
      blogUrl,
      Map(
        "type" -> "photo",
        "caption" -> photoCaption,
        "tags" -> s"Cuttr, glitch, generative, random, ${searchTag}, ${glitchType}"
      ),
      photoData.data,
      photoData.extension
    )
  }

  def checkResponse(response: TumblrResponse[PostId], url: String): Option[Int] = {
    response.meta match {
      case Meta(201, msg) => {
        println("Post url:\n    http://%s/post/%d".format(url, response.response.id))
        Some(0)
      }
      case Meta(status, msg) => {
        println("Status code %d was returned when creating post".format(status))
        println("    %s".format(msg))
        None
      }
    }
  }
}


case class TumblrResponse[T](meta: Meta, response: T)

object TumblrResponse {
  implicit def TumblrResponseDecodeJson[T](implicit typeDecode: DecodeJson[T]): DecodeJson[TumblrResponse[T]] =
    DecodeJson(c => for {
      meta <- (c --\ "meta").as[Meta]
      response <- (c --\ "response").as[T]
    } yield TumblrResponse(meta, response))
}


case class Meta(status: Int, msg: String)

object Meta {
  implicit def MetaCodecJson: CodecJson[Meta] =
    casecodec2(Meta.apply, Meta.unapply)("status", "msg")
}
 
case class PostId(id: Long)

object PostId {
  implicit def PostIdCodecJson: CodecJson[PostId] =
    casecodec1(PostId.apply, PostId.unapply)("id")
}
 
// /info
case class BlogInfo(title: String,
                    posts: Int,
                    name: String,
                    url: String,
                    updated: Long,
                    description: String,
                    ask:Boolean,
                    ask_anon:Boolean,
                    is_nsfw:Boolean,
                    share_likes:Boolean,
                    likes: Int)

object BlogInfo {
  implicit def BlogInfoCodecJson: CodecJson[BlogInfo] =
    casecodec11(BlogInfo.apply, BlogInfo.unapply)("title",
                                                  "posts",
                                                  "name",
                                                  "url",
                                                  "updated",
                                                  "description",
                                                  "ask",
                                                  "ask_anon",
                                                  "is_nsfw",
                                                  "share_likes",
                                                  "likes")
}


// /likes
case class LikedPosts(liked_posts: List[AnyPost], liked_count: Int)

object LikedPosts {
  implicit def LikedPostsCodecJson: CodecJson[LikedPosts] =
    casecodec2(LikedPosts.apply, LikedPosts.unapply)("liked_posts", "liked_count")
}



// /followers

case class Followers(total_users: Int, users: List[User])

object Followers {
  implicit def FollowersCodecJson: CodecJson[Followers] =
    casecodec2(Followers.apply, Followers.unapply)("total_users", "users")
}


case class User(name: String,
                url: String,
                updated: Long)
object User {
  implicit def UserCodecJson: CodecJson[User] =
    casecodec3(User.apply, User.unapply)("name", "url", "updated")
}



case class PostQuery[T](blog:BlogInfo, posts: List[T], total_posts: Int)

object PostQuery {
  implicit def PostQueryDecodeJson[T](implicit typeDecode: DecodeJson[T]): DecodeJson[PostQuery[T]] =
    DecodeJson(c => for {
      blog <- (c --\ "blog").as[BlogInfo]
      posts <- (c --\ "posts").as[List[T]]
      total_posts <- (c --\ "total_posts").as[Int]
    } yield PostQuery(blog, posts, total_posts))
}


// Post types

// This is just a generic class that should cover all post types
case class AnyPost(blog_name: String,
                   id: Long,
                   post_url: String,
                   `type`: String)
object AnyPost {
  implicit def AnyPostCodecJson: CodecJson[AnyPost] =
    casecodec4(AnyPost.apply, AnyPost.unapply)("blog_name", "id", "post_url", "type")
}




case class TextPost(blog_name: String,
                    id: Long,
                    post_url: String,
                    slug: String,
                    `type`: String,
                    date: String,
                    timestamp: Long,
                    state: String,
                    format: String,
                    reblog_key: String,
                    tags: List[String],
                    short_url: String,
                    note_count: Int,
                    title: String,
                    body: String)

object TextPost {
  implicit def TextPostCodecJson: CodecJson[TextPost] =
    casecodec15(TextPost.apply, TextPost.unapply)("blog_name",
                                                  "id",
                                                  "post_url",
                                                  "slug",
                                                  "type",
                                                  "date",
                                                  "timestamp",
                                                  "state",
                                                  "format",
                                                  "reblog_key",
                                                  "tags",
                                                  "short_url",
                                                  "note_count",
                                                  "title",
                                                  "body")
}



case class PhotoPost(blog_name: String,
                     id: Long,
                     post_url: String,
                     slug: String,
                     `type`: String,
                     date: String,
                     timestamp: Long,
                     state: String,
                     format: String,
                     reblog_key: String,
                     tags: List[String],
                     short_url: String,
                     note_count: Int,
                     caption: String,
                     photos: List[Photo])
object PhotoPost {
  implicit def PhotoPostCodecJson: CodecJson[PhotoPost] =
    casecodec15(PhotoPost.apply, PhotoPost.unapply)("blog_name",
                                                    "id",
                                                    "post_url",
                                                    "slug",
                                                    "type",
                                                    "date",
                                                    "timestamp",
                                                    "state",
                                                    "format",
                                                    "reblog_key",
                                                    "tags",
                                                    "short_url",
                                                    "note_count",
                                                    "caption",
                                                    "photos")
}


case class Photo(caption: String, alt_sizes: List[PhotoSize], original_size: PhotoSize)
object Photo {
  implicit def PhotoCodecJson: CodecJson[Photo] =
    casecodec3(Photo.apply, Photo.unapply)("caption", "alt_sizes", "original_size")
}


case class PhotoSize(width: Int, height: Int, url: String)
object PhotoSize {
  implicit def PhotoSizeCodecJson: CodecJson[PhotoSize] =
    casecodec3(PhotoSize.apply, PhotoSize.unapply)("width", "height", "url")
}



