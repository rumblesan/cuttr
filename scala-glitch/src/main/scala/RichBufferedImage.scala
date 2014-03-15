package com.rumblesan.scalaglitch

import java.awt.image.{BufferedImage, ColorModel, WritableRaster}
import java.awt.Color

import RichBufferedImage._
import RichColor._

case class RichBufferedImage(image:BufferedImage) {

  def getPixel(coords:Pair[Int,Int]):Color = {
    val (x, y) = coords
    new Color (image.getRGB(x, y))
  }

  def setPixel(coords:Pair[Int,Int], c:Color):Unit = {
    val (x, y) = coords
    image.setRGB(x, y, c.getRGB())
  }

  def setRed(coords:Pair[Int,Int], r:Int):Unit = {
    val (x, y) = coords
    val c = image.getPixel(x, y)
    val newC = new Color(r, c.getGreen(), c.getBlue())
    image.setRGB(x, y, newC.getRGB())
  }

  def setGreen(coords:Pair[Int,Int], g:Int):Unit = {
    val (x, y) = coords
    val c = image.getPixel(x, y)
    val newC = new Color(c.getRed(), g, c.getBlue())
    image.setRGB(x, y, newC.getRGB())
  }

  def setBlue(coords:Pair[Int,Int], b:Int):Unit = {
    val (x, y) = coords
    val c = image.getPixel(x, y)
    val newC = new Color(c.getRed(), c.getGreen(), b)
    image.setRGB(x, y, newC.getRGB())
  }

  def addPixel(coords:Pair[Int,Int], c:Color):Unit = {
    val (x, y) = coords
    val currentColor = image.getPixel(x,y)
    val newColor = currentColor + c
    image.setRGB(x, y, newColor.getRGB())
  }

  def getSize():Pair[Int, Int] = {
    val width  = image.getWidth()
    val height = image.getHeight()
    (width, height)
  }

  def deepClone():BufferedImage = {
    new BufferedImage(image.getColorModel(),
                      image.copyData(null),
                      image.isAlphaPremultiplied(),
                      null)
  }

  def createGlitch(glitchFunction: BufferedImage => BufferedImage): BufferedImage = {
    glitchFunction(image.deepClone())
  }

}

object RichBufferedImage {

  implicit def bufferedImage2RichBufferedImage(image:BufferedImage):RichBufferedImage = RichBufferedImage(image)

}


