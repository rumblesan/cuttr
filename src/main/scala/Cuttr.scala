package com.rumblesan.scala_images

import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import java.io.IOException;
import scala.util.Random
import java.util.Date

object Cuttr {
  def main(args: Array[String]) {


    val input = try {
      Some(ImageIO.read(new File("landscape.jpeg")))
    } catch {
      case ioe: IOException => {
        None
      }
    }

    val loadedImage = input.get

    val image = new PixelImage(loadedImage)

    val timestamp = new Date().getTime()
    val rand = new Random(timestamp)

    val (width, height) = image.getSize()
    val shifter = PixelTransform.shift(width/7, height/4, 56, 3)

    val output = new NewPixelImage(width, height, image.getType())

    for (x <- 0 until width) {
      for (y <- 0 until height) {
        val c = image.getPixel(x, y)
        val (newx, newy) = shifter(x, y)
        val prevc = image.getPixel(newx, newy)
        output.setPixel(newx, newy, new Color(0, c.getGreen(), prevc.getBlue()))
      }
    }

    print("Writing")
    ImageIO.write(output, "jpg", new File("output.jpeg"))

  }
}


