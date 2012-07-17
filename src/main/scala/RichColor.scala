package com.rumblesan.scala_images

import java.awt.Color

case class RichColor(color:Color) {

  def scale(factor:Double) = {
    val r = color.getRed() * factor
    val g = color.getGreen() * factor
    val b = color.getBlue() * factor
    new Color(r.toInt, g.toInt, b.toInt)
  }

  def *(factor:Double) = scale(factor)

  def +(addition:Color) = {
    val r = color.getRed() + addition.getRed()
    val g = color.getGreen() + addition.getGreen()
    val b = color.getBlue() + addition.getBlue()
    new Color((r % 256), (g % 256), (b % 256))
  }

}

object RichColor {

  implicit def Color2RichColor(color:Color) = RichColor(color)
}

