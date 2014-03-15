package com.rumblesan.scalaglitch.glitches

import scala.util.Random
import math.cos

import com.rumblesan.scalaglitch.RichBufferedImage._
import com.rumblesan.scalaglitch.CoordWrapper.wrapCoords
import com.rumblesan.scalaglitch.RichColor._

import java.awt.image.BufferedImage

object Smear {

  def apply(image: BufferedImage): BufferedImage = {
    val rand = new Random()

    val randXShift = rand.nextDouble() * 100
    val randYShift = rand.nextDouble() * 100

    val (width, height) = image.getSize()

    val shiftXFunc = (pos:Int) => {
      (randXShift + pos + (100 * cos(pos))).toInt
    }
    val shiftYFunc = (pos:Int) => {
      (randYShift + pos + (10 * cos(pos))).toInt
    }

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

}

