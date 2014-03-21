package com.rumblesan.scalaglitch.glitches

import com.rumblesan.scalaglitch.types._

import scalaz._, Scalaz._

import scala.util.Random
import math.{cos, sin, Pi}

import com.rumblesan.scalaglitch.util.RichBufferedImage._
import com.rumblesan.scalaglitch.util.CoordWrapper.wrapCoords
import com.rumblesan.scalaglitch.util.RichColor._

import java.awt.image.BufferedImage


object Pusher extends GlitchTypes {

  def apply(image: BufferedImage): GlitchedGif = {
 
    val rand = new Random()

    val (width, height) = image.getSize()

    val genGlitches = for {
      frames <- getFrameCount
      multiplier <- getFrameOffsetMultiplier
      frameOffset = (Pi / frames).toInt
      shifterGen <- shifterGenerator(width)(height)
      images = for {
        f <- (1 to frames)
        offset = sin(f * frameOffset) * multiplier
        shifter = shifterGen(offset)
        runPusher = pusherGenerator(shifter)
      } yield image.createGlitch(runPusher)
    } yield images

    GlitchedGif(genGlitches.eval(rand).toList)

  }

  val getFrameCount = State[Random, Int] { rand =>
    (rand, rand.nextInt(300) + 100)
  }

  val getFrameOffsetMultiplier = State[Random, Int] { rand =>
    (rand, rand.nextInt(100))
  }

  val createShift: Int => State[Random, Int] = shiftMultiplier => State[Random, Int] { rand =>
    (rand, (rand.nextDouble() * shiftMultiplier).toInt)
  }


  val shifterGenerator: Int => Int => State[Random, Double => PixelShifter] = width => height => for {
    randXShift <- createShift(100)
    randYShift <- createShift(100)
    xShifter = (mult: Double) => createShifter(randXShift)(mult)
    yShifter = (mult: Double) => createShifter(randYShift)(mult)
  } yield (mult: Double) => wrapCoords(width - 1, height - 1, xShifter(mult), yShifter(mult))

  val createShifter: Int => Double => (Int => Int) = randShift => multiplier => {
    (pos) => {
      (randShift + pos + (multiplier * cos(pos))).toInt
    }
  }

  val pusherGenerator: PixelShifter => BufferedImage => BufferedImage = shifter => image => {

    val (width, height) = image.getSize()

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

