package com.rumblesan.scalaglitch.glitches

import scalaz._, Scalaz._

import scala.io.{Source, Codec}

import scala.util.Random

import com.rumblesan.scalaglitch.types._
import com.rumblesan.scalaglitch.util._

import com.rumblesan.scalaglitch.util.RichBufferedImage._
import com.rumblesan.scalaglitch.util.RichColor._

import java.awt.image.BufferedImage

object Fade extends GlitchTypes {

  def apply(image: BufferedImage): GlitchedImage = GlitchedJpeg(image.createGlitch(runFade))

  val runFade: BufferedImage => BufferedImage = image => {

    val glitchedImage = JpegGlitcher(image, 70)

    val (width, height) = image.getSize()

    val r = new Random()

    val upperThresh: Double = (r.nextInt(50) + 20) / 100.0
    val lowerThresh: Double = upperThresh * 0.9
    val fadeDiv: Double = upperThresh - lowerThresh

    for (x <- 0 until width) {
      for (y <- 0 until height) {
        val coords = (x, y)

        val brightness = image.getBrightness(coords)
        if (brightness > upperThresh) {
          image.setPixel(coords, glitchedImage.getPixel(coords))
        } else if (brightness < lowerThresh) {
          // Do Nothing
        } else {
          val fadeVal: Double = ((brightness - lowerThresh) / fadeDiv)
          val originalPx = image.getPixel(coords).scale(1.0 - fadeVal)
          val glitchedPx = glitchedImage.getPixel(coords).scale(fadeVal)
          image.setPixel(coords, (originalPx + glitchedPx))
        }

      }
    }

    image
  }

}

