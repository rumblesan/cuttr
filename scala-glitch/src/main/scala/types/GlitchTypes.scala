package com.rumblesan.scalaglitch.types

import java.awt.image.BufferedImage

trait GlitchTypes {

  type PixelShifter = Pair[Int, Int] => Pair[Int, Int]

}

object GlitchedImages {

  sealed trait GlitchedImage
  case class GlitchedJpeg(image: BufferedImage) extends GlitchedImage

}

