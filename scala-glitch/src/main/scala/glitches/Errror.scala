package com.rumblesan.scalaglitch.glitches

import scalaz._, Scalaz._

import scala.util.Random
import scala.io.{Source, Codec}

import scodec.bits._

import com.rumblesan.scalaglitch.types._
import com.rumblesan.scalaglitch.util._

import java.io.{File, IOException, ByteArrayInputStream}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


object JpegGlitcher {

  def glitchQuantTable(struct: JpegStructureData, glitchChance: Int): JpegStructureData = {

    val r = new Random()
    val glitched = struct.data.drop(3).map(b => {
      if (r.nextInt(100) < glitchChance) {
        (r.nextInt(253) + 1).toByte
      } else {
        b
      }
    })

    struct.copy(data = (struct.data.take(3) ++ glitched))

  }

}


object Errror extends GlitchTypes {

  def apply(imageFile: File): GlitchedImage = {

    implicit val codec = Codec.ISO8859

    val bytes: ByteVector = ByteVector(Source.fromFile(imageFile).map(_.toByte).toArray)

    val sections = JpegParser.parse(bytes).map(s => {
      if (s.name == "DQT") {
        JpegGlitcher.glitchQuantTable(s, 40)
      } else {
        s
      }
    })
    sections.foreach{ s => println(s.name) }

    val dataout = sections.foldLeft(ByteVector.empty)((out, sect) => {
      out ++ JpegParser.writeData(sect)
    })

    val bufferedImage = ImageIO.read(new ByteArrayInputStream(dataout.toArray))

    GlitchedJpeg(bufferedImage)
  }


}

