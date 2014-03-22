package com.rumblesan.scalaglitch.util

import com.rumblesan.scalaglitch.types._

import java.awt.image.BufferedImage

import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import java.io.{File, IOException}


object FileOps {

  def glitchedImageToByteArray(image: GlitchedImage): GlitchedImageData = {
    image match {
      case GlitchedJpeg(jpeg) => glitchedJpegToByteArray(jpeg)
      case GlitchedGif(frames) => glitchedGifToByteArray(frames)
    }
  }

  def glitchedJpegToByteArray(jpeg: BufferedImage): GlitchedImageData = {
    val baos = new ByteArrayOutputStream()
    ImageIO.write(jpeg, "jpg", baos)
    baos.flush()
    val imageData = baos.toByteArray()
    baos.close()
    GlitchedImageData(imageData, "jpeg")
  }

  def glitchedGifToByteArray(gifFrames: List[BufferedImage]): GlitchedImageData = {

    val baos = new ByteArrayOutputStream()
    val gifEncoder = new AnimatedGifEncoder()
    gifEncoder.start(baos)
    gifEncoder.setRepeat(0)
    gifEncoder.setFrameRate(30f)

    var count = 1
    for (img <- gifFrames) {
      gifEncoder.addFrame(img)
      println(s"written $count")
      count += 1
    }
    gifEncoder.finish()

    val imageData = baos.toByteArray()
    baos.close()
    GlitchedImageData(imageData, "gif")

    imageData

  }

}

