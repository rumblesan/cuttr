package com.rumblesan.scala_images

import java.awt.image.{BufferedImage, ColorModel, WritableRaster}
import java.awt.Color

case class RichBufferedImage(image:BufferedImage) {

  def getPixel(xVal:Int, yVal:Int) = {
    new Color (image.getRGB(xVal, yVal))
  }

  def setPixel(xVal:Int, yVal:Int, c:Color) = {
    image.setRGB(xVal, yVal, c.getRGB())
  }

  def getSize() = {
    val width  = image.getWidth()
    val height = image.getHeight()
    (width, height)
  }

}

object RichBufferedImage {

  implicit def bufferedImage2RichBufferedImage(image:BufferedImage) = RichBufferedImage(image)

}


