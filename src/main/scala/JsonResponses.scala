package com.rumblesan.cuttr

abstract class TumblrApiResponse()

case class TumblrResponse(meta:Meta, response:Option[Any])
case class Meta(status:Int, msg:String)

case class TumblrPostResponse(meta:Meta, response:Option[PostId])
case class PostId(id:Long) extends TumblrApiResponse()

case class TumblrInfoQueryResponse(meta:Meta, response:Option[InfoQuery])

case class InfoQuery(blog:BlogInfo) extends TumblrApiResponse()

// All GET responses have a BlogInfo section
case class BlogInfo(title:String,
                    posts:Int,
                    name:String,
                    url:String,
                    updated:Long,
                    description:String,
                    ask:Boolean,
                    followed:Boolean,
                    can_send_fan_mail:Boolean)



case class TumblrAnyQueryResponse(meta:Meta, response:Option[AnyQuery])
// This is just a generic class that should cover all post types
case class AnyQuery(blog:BlogInfo, posts:List[AnyPost], total_posts:Int) extends TumblrApiResponse()
case class AnyPost(blog_name:String,
                   id:Long,
                   post_url:String,
                   `type`:String)

case class TumblrPhotoQueryResponse(meta:Meta, response:Option[PhotoQuery])
case class PhotoQuery(blog:BlogInfo, posts:List[PhotoPost], total_posts:Int) extends TumblrApiResponse()
case class PhotoPost(blog_name:String,
                     id:Long,
                     post_url:String,
                     slug:String,
                     `type`:String,
                     date:String,
                     timestamp:Long,
                     state:String,
                     format:String,
                     reblog_key:String,
                     tags:List[String],
                     liked:Boolean,
                     caption:String,
                     photos:List[Photo],
                     can_reply:Boolean)
case class Photo(caption:String, alt_sizes:List[PhotoSizes])
case class PhotoSizes(width:Int, height:Int, url:String)

