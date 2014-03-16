package com.rumblesan.cuttr.models

case class CuttrPhoto(imgUrl: String, blogName: String, postUrl: String, postDate: String)

trait CuttrPhotoOps {

  def caption: String = {
    """
      <a href='${self.imgUrl}'>Original</a> image courtesy of ${self.blogName}
      <a href='${self.postUrl}'>First posted</a> on ${self.postDate}
    """
  }

}

trait ToCuttrPhotoOps {

  implicit def ToCuttrPhotoOps(p: CuttrPhoto): CuttrPhotoOps =
    new CuttrPhotoOps {
      def self = p
    }

}

object CuttrPhoto extends ToCuttrPhotoOps

