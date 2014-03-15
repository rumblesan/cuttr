package com.rumblesan.scalaglitch

import glitches._

import java.awt.image.BufferedImage


object Glitchr {

  def apply(image: BufferedImage, glitchType: String): BufferedImage = {
    glitchType match {
      case "smear" => smear(image)
      case "cubist" => smear(image)
      case _ => smear(image)
    }
  }

  def smear(image: BufferedImage): BufferedImage = Smear(image)

  def pusher(image: BufferedImage): List[BufferedImage] = Pusher(image)

  def cubist(image: BufferedImage): BufferedImage = Cubist(image)

}




