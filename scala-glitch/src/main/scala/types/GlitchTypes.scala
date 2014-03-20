package com.rumblesan.scalaglitch.types

import java.awt.image.BufferedImage

trait GlitchTypes {

  type PixelShifter = Pair[Int, Int] => Pair[Int, Int]

}


sealed trait GlitchedImage
case class GlitchedJpeg(image: BufferedImage) extends GlitchedImage

sealed trait GlitchSource
case class ImageCanvas(image: BufferedImage, glitchType: String) extends GlitchSource

