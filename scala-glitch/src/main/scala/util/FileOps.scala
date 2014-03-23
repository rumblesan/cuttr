package com.rumblesan.scalaglitch.util

import com.rumblesan.scalaglitch.types._

import java.awt.image.BufferedImage

import javax.imageio.ImageIO
import java.io.{ByteArrayOutputStream, FileOutputStream, BufferedOutputStream}
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
    gifEncoder.setQuality(30)
    gifEncoder.setRepeat(0)

    var count = 1
    gifEncoder.setDelay(4000)
    gifEncoder.addFrame(gifFrames.head)
    gifEncoder.setFrameRate(10f)
    for (img <- gifFrames.tail) {
      gifEncoder.addFrame(img)
      println(s"written $count")
      count += 1
    }
    gifEncoder.finish()

    val imageData = baos.toByteArray()
    baos.close()
    GlitchedImageData(imageData, "gif")

  }

  def glitchedImageToFile(filename: String, data: GlitchedImageData): File = {

    val outputFile = new File(s"$filename.${data.extension}")
    val fos = new FileOutputStream(outputFile)
    val bos = new BufferedOutputStream(fos)

    bos.write(data.data);
    bos.close()

    outputFile
  }

}

