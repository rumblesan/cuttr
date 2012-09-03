package com.rumblesan.cuttr

import java.awt.image.{BufferedImage, ColorModel, WritableRaster}
import java.awt.Color

import RichBufferedImage._
import RichColor._

case class RichBufferedImage(image:BufferedImage) {

  def getPixel(coords:Pair[Int,Int]):Color = {
    val (x, y) = coords
    new Color (image.getRGB(x, y))
  }

  def getPixel(coords:Pair[Double,Double]):Color = {
    val (x, y) = coords
    val a = image.getPixel((x.toInt + 0), (y.toInt + 0))
    val b = image.getPixel((x.toInt + 1), (y.toInt + 0))
    val c = image.getPixel((x.toInt + 0), (y.toInt + 1))
    val d = image.getPixel((x.toInt + 1), (y.toInt + 1))

    (a * (1 - (x % 1)) * (1 - (y % 1))) +
    (b * ((x % 1)) * (1 - (y % 1))) +
    (c * (1 - (x % 1)) * ((y % 1))) +
    (d * ((x % 1)) * ((y % 1)))
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

  def addPixel(coords:Pair[Double,Double], c:Color):Unit = {
    val (x, y) = coords
    val a = (1 - (x % 1)) * (1 - (y % 1))
    val b = ((x % 1)) * (1 - (y % 1))
    val c = (1 - (x % 1)) * ((y % 1))
    val d = ((x % 1)) * ((y % 1))

    val pA = image.getPixel((x.toInt + 0), (y.toInt + 0))
    val pB = image.getPixel((x.toInt + 1), (y.toInt + 0))
    val pC = image.getPixel((x.toInt + 0), (y.toInt + 1))
    val pD = image.getPixel((x.toInt + 1), (y.toInt + 1))

    image.setPixel((x.toInt + 0), (y.toInt + 0), pA * a)
    image.setPixel((x.toInt + 1), (y.toInt + 0), pB * b)
    image.setPixel((x.toInt + 0), (y.toInt + 1), pC * c)
    image.setPixel((x.toInt + 1), (y.toInt + 1), pD * d)
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

}

object RichBufferedImage {

  implicit def bufferedImage2RichBufferedImage(image:BufferedImage):RichBufferedImage = RichBufferedImage(image)

}


