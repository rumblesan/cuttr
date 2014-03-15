package com.rumblesan.scalaglitch.util

import com.rumblesan.scalaglitch.types.GlitchedImages._

import java.awt.image.BufferedImage

import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import java.io.{File, IOException}


object FileOps {

  def glitchedImageToByteArray(image: GlitchedImage): Array[Byte] = {
    image match {
      case GlitchedJpeg(jpeg) => glitchedJpegToByteArray(jpeg)
    }
  }

  def glitchedJpegToByteArray(jpeg: BufferedImage): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    ImageIO.write(jpeg, "jpg", baos)
    baos.flush()
    val imageData = baos.toByteArray()
    baos.close()
    imageData
  }


}

