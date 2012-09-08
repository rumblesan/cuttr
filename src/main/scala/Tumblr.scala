package com.rumblesan.cuttr.tumblr

/** These are classes mostly used for decoding json data from Tumblr
  */

case class Blog(blog:BlogInfo)

case class BlogPosts(blog:BlogInfo, posts:List[PhotoPost], total_posts:Int)

case class BlogInfo(title:String,
                    posts:Int,
                    name:String,
                    url:String,
                    updated:Long,
                    description:String,
                    ask:Boolean,
                    ask_anon:Boolean,
                    followed:Boolean,
                    can_send_fan_mail:Boolean)

case class PhotoPost(blog_name:String,
                     id:Long,
                     post_url:String,
                     `type`:String,
                     date:String,
                     timestamp:Long,
                     format:String,
                     reblog_key:String,
                     tags:List[String],
                     note_count:Int,
                     caption:String,
                     photos:List[Photo])
case class Photo(caption:String, alt_sizes:List[PhotoSizes])
case class PhotoSizes(width:Int, height:Int, url:String)

case class Meta(status:Int, msg:String)

case class BlogInfoAPICall(meta:Meta, response:Blog)

case class BlogPhotoAPICall(meta:Meta, response:BlogPosts)

