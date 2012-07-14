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
    val shifter = PixelTransform.shift(width-1, height-1, 5, 3)

    val output = new NewPixelImage(width, height, image.getType())

    for (x <- 0 until width) {
      for (y <- 0 until height) {

        val originalColor = image.getPixel(x, y)

        val (redX, redY) = shifter(x, y)
        val (greenX, greenY) = shifter(redX, redY)

        val prevRed = image.getPixel(redX, redY)
        val prevGreen = image.getPixel(greenX, greenY)

        output.setPixel(x, y, new Color(prevRed.getRed(), prevGreen.getGreen(), originalColor.getBlue()))
      }
    }

    print("Writing")
    ImageIO.write(output, "jpg", new File("output.jpeg"))

  }
}


