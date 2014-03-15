package com.rumblesan.scalaglitch.glitches

import scalaz._, Scalaz._

import scala.util.Random
import math.cos

import com.rumblesan.scalaglitch.RichBufferedImage._
import com.rumblesan.scalaglitch.CoordWrapper.wrapCoords
import com.rumblesan.scalaglitch.RichColor._

import java.awt.image.BufferedImage

object Cubist {

  type PixelShifter = Pair[Int, Int] => Pair[Int, Int]

  def apply(image: BufferedImage): BufferedImage = {

    val randState = new Random()
    val (width, height) = image.getSize()

    // Really we should have a way to pass the state through each of
    // these one after the other. As it is, this is depending on the
    // global state of the Random object still
    val shifters: Seq[PixelShifter] = (1 to 30).map(
      v => createPixelTransform(width, height).eval(randState)
    )

    for (x <- 0 until width) {
      for (y <- 0 until height) {
        for (s <- shifters) {
          image.setPixel((x, y), image.getPixel(s((x, y))))
        }
      }
    }

    image
  }

  def createPixelTransform(width: Int, height: Int): State[Random, PixelShifter] = {

    for {
      xShiftValue <- createShiftValue(width)
      yShiftValue <- createShiftValue(height)
      xShifter <- createPixelShifter(xShiftValue, width)
      yShifter <- createPixelShifter(yShiftValue, height)
    } yield wrapCoords(width - 1, height - 1, xShifter, yShifter)

  }

  def createPixelShifter(shiftDistance: Int, maxSize: Int): State[Random, Int => Int] = {
    for {
      cutSize1 <- createCutSize(maxSize)
      cutSize2 <- createCutSize(maxSize)
    } yield (pos: Int) => {
      if (pos < cutSize1 && pos > cutSize2) (pos + shiftDistance)
      else pos
    }
  }

  def createCutSize(maxSize: Int): State[Random, Int] = State[Random, Int] { rand =>
    val size = (rand.nextDouble() * (maxSize / 4)) + (maxSize / 5)
    (rand, size.toInt)
  }

  def createShiftValue(maxSize: Int): State[Random, Int] = State[Random, Int] { rand =>
    val shift = rand.nextDouble() * (maxSize / 3)
    (rand, shift.toInt)
  }

}

