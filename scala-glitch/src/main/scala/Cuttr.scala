package com.rumblesan.scalaglitch

import glitches._

import javax.imageio.ImageIO

import java.awt.Color
import java.awt.image.BufferedImage

import RichBufferedImage._
import CoordWrapper.wrapCoords
import RichColor._

import scala.util.Random
import math._

import Numeric._


class Cuttr(image:BufferedImage) {

  val cuttrrand = new Random()

  def glitch(): BufferedImage = {
    cubist()
  }


  def smear(): BufferedImage = Smear(image)

  def pusher(): List[BufferedImage] = Pusher(image)

  def cubist(): BufferedImage = Cubist(image)


}




