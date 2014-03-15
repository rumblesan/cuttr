package com.rumblesan.scalaglitch.glitches

import scalaz._, Scalaz._

import scala.util.Random
import math.cos

import com.rumblesan.scalaglitch.util.RichBufferedImage._
import com.rumblesan.scalaglitch.util.CoordWrapper.wrapCoords
import com.rumblesan.scalaglitch.util.RichColor._

import com.rumblesan.scalaglitch.types.GlitchTypes
import com.rumblesan.scalaglitch.types.GlitchedImages._

import java.awt.image.BufferedImage

object Cubist extends GlitchTypes {

  def apply(image: BufferedImage): GlitchedImage = GlitchedJpeg(image.createGlitch(runCubist))

  lazy val runCubist: BufferedImage => BufferedImage = image => {

    val randState = new Random()
    val (width, height) = image.getSize()

    // traverseS allows us to thread the state through all the
    // state monads in the list
    val shifters: List[PixelShifter] = (1 to 30).toList.traverseS(
      v => createPixelTransform(width, height)
    ).eval(randState)

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

