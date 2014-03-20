package com.rumblesan.scalaglitch

import glitches._
import types._
import util.FileOps

import java.awt.image.BufferedImage


object Glitchr extends GlitchTypes {

  def apply(image: GlitchSource): Array[Byte] = {

    val glitchedImage: GlitchedImage = image match {
      case ImageCanvas(source, glitchType) => canvasGlitch(source, glitchType)
    }

    FileOps.glitchedImageToByteArray(glitchedImage)
  }

  def canvasGlitch(image: BufferedImage, glitchType: String): GlitchedImage = {
    glitchType match {
      case "smear" => smear(image)
      case "cubist" => cubist(image)
      case _ => smear(image)
    }
  }

  def smear(image: BufferedImage): GlitchedImage = Smear(image)

  def pusher(image: BufferedImage): List[BufferedImage] = Pusher(image)

  def cubist(image: BufferedImage): GlitchedImage = Cubist(image)

}




