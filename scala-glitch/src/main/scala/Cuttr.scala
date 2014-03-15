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

  def pushed(): List[BufferedImage] = {

    val rand = new Random()

    val frames = rand.nextInt(100) + 10

    val shiftGenerator: Int => Int => (Int => Int) = shift => multiplier => {
      (pos: Int) => {
        (shift + pos + (multiplier * cos(pos))).toInt
      }
    }

    val glitcher: BufferedImage => (Int => Int) => (Int => Int) => BufferedImage = image => shiftXFunc => shiftYFunc => {

      val (width, height) = image.getSize()
      val shifter = wrapCoords(width-1, height-1, shiftXFunc, shiftYFunc)

      for (x <- 0 until width) {
        for (y <- 0 until height) {

          val coords = (x, y)

          val rShiftCoords = shifter(coords)
          val gShiftCoords = shifter(rShiftCoords)
          val bShiftCoords = shifter(gShiftCoords)

          val newRed   = image.getPixel(rShiftCoords)
          val newGreen = image.getPixel(gShiftCoords)
          val newBlue  = image.getPixel(bShiftCoords)

          image.setRed(coords, newRed.getRed())
          image.setGreen(coords, newGreen.getGreen())
          image.setBlue(coords, newBlue.getBlue())

        }
      }

    image

    }

    for {
      f <- (1 to frames)
    } yield f

    List(image)

  }

  def cubist(): BufferedImage = Cubist(image)


}




