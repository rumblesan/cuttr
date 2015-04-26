package com.rumblesan.scalaglitch.util

import java.awt.{Image, Graphics}
import java.awt.image.{BufferedImage, ColorModel, WritableRaster}
import java.awt.Color
import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream


import scodec.bits._

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

  def getBrightness(coords: (Int, Int)): Double = {
    image.getPixel(coords).getBrightness()
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

  def setMaxHeight(maxHeight: Int): BufferedImage = {
    val (fullWidth, fullHeight) = image.getSize()
    val newHeight = maxHeight
    val newWidth = ((newHeight.toDouble / fullHeight) * fullWidth).toInt
    val newImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)

    val bImage = new BufferedImage(newImage.getWidth(null), newImage.getHeight(null), BufferedImage.TYPE_INT_ARGB)
    val bgr = bImage.createGraphics()
    bgr.drawImage(newImage, 0, 0, null)
    bgr.dispose()
    bImage
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

  def getJpegBytes: ByteVector = {
    val baos = new ByteArrayOutputStream()
    ImageIO.write(image, "jpg", baos)
    baos.flush()
    val output = ByteVector(baos.toByteArray())
    baos.close();
    output
  }

}

object RichBufferedImage {

  implicit def bufferedImage2RichBufferedImage(image:BufferedImage):RichBufferedImage = RichBufferedImage(image)

}


