package com.rumblesan.scalaglitch.glitches

import scalaz._, Scalaz._

import scala.util.Random
import math.cos

import com.rumblesan.scalaglitch.util.RichBufferedImage._
import com.rumblesan.scalaglitch.util.CoordWrapper.wrapCoords
import com.rumblesan.scalaglitch.util.RichColor._

import java.awt.image.BufferedImage

object Smear {

  type PixelShifter = Pair[Int, Int] => Pair[Int, Int]

  def apply(image: BufferedImage): BufferedImage = image.createGlitch(runSmear)

  val runSmear: BufferedImage => BufferedImage = image => {

    val randState = new Random()

    val (width, height) = image.getSize()

    val shifter = shifterGenerator(width)(height).eval(randState)

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

  val shifterGenerator: Int => Int => State[Random, PixelShifter] = width => height => for {
    randXShift <- createShift(100)
    randYShift <- createShift(100)
    xShifter = createShifter(randXShift)(100)
    yShifter = createShifter(randYShift)(10)
  } yield wrapCoords(width - 1, height - 1, xShifter, yShifter)

  val createShift: Int => State[Random, Int] = shiftMultiplier => State[Random, Int] { rand =>
    (rand, (rand.nextDouble() * shiftMultiplier).toInt)
  }

  val createShifter: Int => Int => (Int => Int) = randShift => multiplier => {
    (pos) => {
      (randShift + pos + (multiplier * cos(pos))).toInt
    }
  }

}

