package com.rumblesan.cuttr

import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.{File, IOException}
import scala.util.Random
import math._

import RichBufferedImage._
import RichColor._

object Cuttr {

  def main(args: Array[String]) {
    Arguments(args) map { config =>
      run(config)
    }
  }


  def run(config:Config) {

    val inputfile = try {
      Some(ImageIO.read(new File(config.inputfile)))
    } catch {
      case ioe: IOException => {
        None
      }
    }

    inputfile map { image =>

      val rand = new Random()
      val randXShift = rand.nextDouble() * 100
      val randYShift = rand.nextDouble() * 100

      val (width, height) = image.getSize()

      val shiftXFunc = (pos:Int) => {randXShift + pos + (10 * cos(pos))}
      val shiftYFunc = (pos:Int) => {randYShift + pos + (10 * cos(pos))}

      val shifter = PixelTransform.funcVar(width-1, height-1, shiftXFunc, shiftYFunc)

      val output = new BufferedImage(width, height, image.getType())

      for (x <- 0 until width) {
        for (y <- 0 until height) {

          val originalColor = image.getPixel(x, y)

          val (redX, redY) = shifter(x, y)
          val (greenX, greenY) = shifter(redX.toInt, redY.toInt)

          val prevRed = image.getPixel(redX, redY).scale(0.5)
          val prevGreen = image.getPixel(greenX, greenY).scale(0)

          output.setPixel(x, y, new Color(prevRed.getRed(), prevGreen.getGreen(), originalColor.getBlue()))
        }
      }

      ImageIO.write(output, "jpg", new File("output.jpeg"))

    }

  }

}

