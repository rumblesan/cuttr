package com.rumblesan.scalaglitch

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


  def smear(): BufferedImage = {
      val rand = new Random()

      val randXShift = rand.nextDouble() * 100
      val randYShift = rand.nextDouble() * 100

      val (width, height) = image.getSize()

      val shiftXFunc = (pos:Int) => {
        (randXShift + pos + (100 * cos(pos))).toInt
      }
      val shiftYFunc = (pos:Int) => {
        randYShift + pos + (10 * cos(pos))
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

  def cubist(): BufferedImage = {

      val (width, height) = image.getSize()

      val shifters = (1 to 30).map(v => createPixelTransform(width, height))

      for (x <- 0 until width) {
        for (y <- 0 until height) {

          val coords = (x, y)

          for (s <- shifters) {
            image.setPixel(coords, image.getPixel(s(coords)))
          }

        }
      }

    image

  }

  def createPixelTransform(width: Int, height: Int) = {

    val randXShift = cuttrrand.nextDouble() * (width / 3)
    val randYShift = cuttrrand.nextDouble() * (height / 3)

    val xShifter = createPixelShifter(randXShift.toInt, width)
    val yShifter = createPixelShifter(randYShift.toInt, height)

    wrapCoords(width-1, height-1, xShifter, yShifter)

  }

  def createPixelShifter(dist: Int, size: Int): (Int) => Double = {

    val rand1 = (cuttrrand.nextDouble() * (size / 4)) + (size / 5)
    val rand2 = (cuttrrand.nextDouble() * (size / 4)) + (size / 3)

    (pos:Int) => {

      if (pos < rand2 && pos > rand1) {
        (pos + dist).toDouble
      } else {
        pos.toDouble
      }

    }


  }


}




