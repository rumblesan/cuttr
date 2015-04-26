package com.rumblesan.scalaglitch.glitches

import scalaz._, Scalaz._

import com.rumblesan.scalaglitch.types._
import com.rumblesan.scalaglitch.util._

import java.awt.image.BufferedImage


object Errror extends GlitchTypes {

  def apply(image: BufferedImage): GlitchedImage = GlitchedJpeg(JpegGlitcher(image, 40))

}

