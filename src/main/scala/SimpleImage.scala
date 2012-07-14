package com.rumblesan.scala_images

import java.awt.image.{BufferedImage, ColorModel, WritableRaster}
import java.awt.Color
import java.util.Hashtable

/** Should be able to combine these two classes, just not sure how
 *  Multiple parent class constructor overriding is confusing
 */

class PixelImage(cm:ColorModel, wr:WritableRaster, mult:Boolean, ops:Hashtable[String,String]) extends BufferedImage(cm, wr, mult, ops) {

  def this(img:BufferedImage) = this(img.getColorModel(), img.getRaster(), img.isAlphaPremultiplied, null)

  def getPixel(xVal:Int, yVal:Int) = {
    new Color (getRGB(xVal, yVal))
  }

  def setPixel(xVal:Int, yVal:Int, c:Color) = {
    setRGB(xVal, yVal, c.getRGB())
  }

  def getSize() = {
    val width  = getWidth()
    val height = getHeight()
    (width, height)
  }

}

// Third arg might not be color type, can't remember right now
class NewPixelImage(width:Int, height:Int, colorType:Int) extends BufferedImage(width, height, colorType) {

  def getPixel(xVal:Int, yVal:Int) = {
    new Color (getRGB(xVal, yVal))
  }

  def setPixel(xVal:Int, yVal:Int, c:Color) = {
    setRGB(xVal, yVal, c.getRGB())
  }

  def getSize() = {
    val width  = getWidth()
    val height = getHeight()
    (width, height)
  }

}




