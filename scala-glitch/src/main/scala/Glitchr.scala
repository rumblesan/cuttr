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

  def canvasGlitch(image: BufferedImage, glitchType: String): GlitchedImage = {
    glitchType match {
      case "smear" => smear(image)
      case "cubist" => cubist(image)
      case "pusher" => pusher(image)
      case "errror" => errror(image)
      case "fade" => fade(image)
      case _ => smear(image)
    }
  }

  def smear(image: BufferedImage): GlitchedImage = Smear(image)

  def pusher(image: BufferedImage): GlitchedImage = Pusher(image)

  def cubist(image: BufferedImage): GlitchedImage = Cubist(image)

  def errror(image: BufferedImage): GlitchedImage = {
    val f = new File("temp.jpg")
    ImageIO.write(image, "jpg", f);
    Errror(f)
  }

  def fade(image: BufferedImage): GlitchedImage = Fade(image)

}




