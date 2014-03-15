package com.rumblesan.scalaglitch.util

import java.awt.Color

case class RichColor(color:Color) {

  def scale(factor:Double):Color = {
    val r = color.getRed() * factor
    val g = color.getGreen() * factor
    val b = color.getBlue() * factor
    new Color(r.toInt.min(255), g.toInt.min(255), b.toInt.min(255))
  }

  def *(factor:Double):Color = scale(factor)

  def +(addition:Color):Color = {
    val r = color.getRed() + addition.getRed()
    val g = color.getGreen() + addition.getGreen()
    val b = color.getBlue() + addition.getBlue()
    new Color((r.min(255)), (g.min(255)), (b.min(255)))
  }

}

object RichColor {

  implicit def Color2RichColor(color:Color):RichColor = RichColor(color)

}

