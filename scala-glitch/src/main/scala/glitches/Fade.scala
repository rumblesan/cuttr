package com.rumblesan.scalaglitch.glitches

import scalaz._, Scalaz._

import scala.io.{Source, Codec}

import scala.util.Random

import scodec.bits._

import com.rumblesan.scalaglitch.types._
import com.rumblesan.scalaglitch.util._

import com.rumblesan.scalaglitch.util.RichBufferedImage._
import com.rumblesan.scalaglitch.util.CoordWrapper.wrapCoords
import com.rumblesan.scalaglitch.util.RichColor._

import java.io.{File, IOException, ByteArrayInputStream}
import java.awt.image.BufferedImage
import java.awt.Color
import javax.imageio.ImageIO


object Fade extends GlitchTypes {

  def apply(originalImage: BufferedImage): GlitchedImage = {

    val imageFile = new File("temp.jpg")
    ImageIO.write(originalImage, "jpg", imageFile);

    implicit val codec = Codec.ISO8859

    val bytes: ByteVector = ByteVector(Source.fromFile(imageFile).map(_.toByte).toArray)

    val sections = JpegParser.parse(bytes).map(s => {
      if (s.name == "DQT") {
        JpegGlitcher.glitchQuantTable(s, 70)
      } else {
        s
      }
    })

    val dataout = sections.foldLeft(ByteVector.empty)((out, sect) => {
      out ++ JpegParser.writeData(sect)
    })

    val glitchedImage = ImageIO.read(new ByteArrayInputStream(dataout.toArray))

    val (width, height) = originalImage.getSize()
    val r = new Random()
    val upperThresh: Double = (r.nextInt(50) + 20) / 100.0
    val lowerThresh: Double = upperThresh * 0.9
    val fadeDiv: Double = upperThresh - lowerThresh

    for (x <- 0 until width) {
      for (y <- 0 until height) {
        val coords = (x, y)

        val brightness = originalImage.getBrightness(coords)
        if (brightness > upperThresh) {
          originalImage.setPixel(coords, glitchedImage.getPixel(coords))
        } else if (brightness < lowerThresh) {
          // Do Nothing
        } else {
          val fadeVal: Double = ((brightness - lowerThresh) / fadeDiv)
          val originalPx: Color = originalImage.getPixel(coords).scale(1.0 - fadeVal)
          val glitchedPx: Color = glitchedImage.getPixel(coords).scale(fadeVal)
          originalImage.setPixel(coords, (originalPx + glitchedPx))
        }

      }
    }

    

    GlitchedJpeg(originalImage)
  }


}

