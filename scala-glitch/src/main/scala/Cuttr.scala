package com.rumblesan.scalaglitch

import javax.imageio.ImageIO

import java.awt.Color
import java.awt.image.BufferedImage

import RichBufferedImage._
import PixelTransform._
import RichColor._

import scala.util.Random
import math._

class Cuttr(image:BufferedImage) {

  def glitch() = {
      val rand = new Random()
      val randXShift = rand.nextDouble() * 100
      val randYShift = rand.nextDouble() * 100

      val (width, height) = image.getSize()

      val shiftXFunc = (pos:Int) => {randXShift + pos + (100 * cos(pos))}
      val shiftYFunc = (pos:Int) => {randYShift + pos + (10 * cos(pos))}

      val rand1 = rand.nextInt(200)
      val rand2 = rand.nextInt(height - 300) + 300
      val rand3 = rand.nextInt(40)

      val cutFunc = (pos:Int) => {
        if (rand1 < pos & pos < rand2) {
          pos + rand3
        } else {
          pos
        }
      }

      val rand4 = rand.nextInt(300)
      val rand5 = rand.nextInt(height - 100) + 300
      val rand6 = rand.nextInt(40)

      val cutFunc2 = (pos:Int) => {
        if (rand4 < pos & pos < rand5) {
          pos + rand6
        } else {
          pos
        }
      }

      val shifter = pixelTransform(width-1, height-1, shiftXFunc, shiftYFunc)
      val cutter1 = pixelTransform(width-1, height-1, (v:Int) => v, cutFunc)
      val cutter2 = pixelTransform(width-1, height-1, cutFunc2, (v:Int) => v)

      for (x <- 0 until width) {
        for (y <- 0 until height) {

          val coords = (x, y)

          val cut1 = image.getPixel(coords)
          val cut1Coords = cutter1(coords)
          image.setPixel(cut1Coords, cut1)

          val rShiftCoords = shifter(coords)
          val gShiftCoords = shifter(rShiftCoords)
          val bShiftCoords = shifter(gShiftCoords)

          val newRed   = image.getPixel(rShiftCoords)
          val newGreen = image.getPixel(gShiftCoords)
          val newBlue  = image.getPixel(bShiftCoords)

          image.setRed(coords, newRed.getRed())
          image.setGreen(coords, newGreen.getGreen())
          image.setBlue(coords, newBlue.getBlue())

          val cut2 = image.getPixel(coords)
          val cut2Coords = cutter2(coords)
          image.setPixel(cut2Coords, cut2)
        }
      }

    image

  }

}




