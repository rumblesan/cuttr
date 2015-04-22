package com.rumblesan.scalaglitch

import glitches._
import types._
import util.FileOps

import javax.imageio.ImageIO
import java.io.{File, IOException}

import java.awt.image.BufferedImage


object Glitchr extends GlitchTypes {

  def apply(image: GlitchSource): GlitchedImageData = {

    val glitchedImage: GlitchedImage = image match {
      case ImageCanvas(sourceFile, glitchType) => canvasGlitch(sourceFile, glitchType)
    }

    FileOps.glitchedImageToByteArray(glitchedImage)
  }

  def canvasGlitch(imageFile: File, glitchType: String): GlitchedImage = {
    glitchType match {
      case "smear" => smear(ImageIO.read(imageFile))
      case "cubist" => cubist(ImageIO.read(imageFile))
      case "pusher" => pusher(ImageIO.read(imageFile))
      case _ => smear(ImageIO.read(imageFile))
    }
  }

  def smear(image: BufferedImage): GlitchedImage = Smear(image)

  def pusher(image: BufferedImage): GlitchedImage = Pusher(image)

  def cubist(image: BufferedImage): GlitchedImage = Cubist(image)

}




