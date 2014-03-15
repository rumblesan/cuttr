package com.rumblesan.scalaglitch

import glitches._
import types.GlitchTypes
import types.GlitchedImages._
import util.FileOps

import java.awt.image.BufferedImage


object Glitchr extends GlitchTypes {

  def apply(image: BufferedImage, glitchType: String): Array[Byte] = {
    val glitchedImage: GlitchedImage = glitchType match {
      case "smear" => smear(image)
      case "cubist" => smear(image)
      case _ => smear(image)
    }
    FileOps.glitchedImageToByteArray(glitchedImage)
  }

  def smear(image: BufferedImage): GlitchedImage = Smear(image)

  def pusher(image: BufferedImage): List[BufferedImage] = Pusher(image)

  def cubist(image: BufferedImage): GlitchedImage = Cubist(image)

}




