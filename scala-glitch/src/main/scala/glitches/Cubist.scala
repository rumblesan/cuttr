package com.rumblesan.scalaglitch.glitches

import scala.util.Random
import math.cos

import com.rumblesan.scalaglitch.RichBufferedImage._
import com.rumblesan.scalaglitch.CoordWrapper.wrapCoords
import com.rumblesan.scalaglitch.RichColor._

import java.awt.image.BufferedImage

object Cubist {

  val cuttrrand = new Random()

  def apply(image: BufferedImage): BufferedImage = {

    val (width, height) = image.getSize()

    val shifters = (1 to 30).map(v => createPixelTransform(width, height))

    for (x <- 0 until width) {
      for (y <- 0 until height) {
        for (s <- shifters) {
          image.setPixel((x, y), image.getPixel(s((x, y))))
        }
      }
    }

    image
  }

  def createPixelTransform(width: Int, height: Int): Pair[Int, Int] => Pair[Int, Int] = {

    val randXShift = cuttrrand.nextDouble() * (width / 3)
    val randYShift = cuttrrand.nextDouble() * (height / 3)

    val xShifter = createPixelShifter(randXShift.toInt, width)
    val yShifter = createPixelShifter(randYShift.toInt, height)

    wrapCoords(width-1, height-1, xShifter, yShifter)

  }

  def createPixelShifter(dist: Int, size: Int): Int => Int = {

    val rand1 = (cuttrrand.nextDouble() * (size / 4)) + (size / 5)
    val rand2 = (cuttrrand.nextDouble() * (size / 4)) + (size / 3)

    (pos:Int) => {

      if (pos < rand2 && pos > rand1) {
        (pos + dist)
      } else {
        pos
      }

    }


  }

}

