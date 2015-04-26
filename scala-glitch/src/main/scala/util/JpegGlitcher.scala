package com.rumblesan.scalaglitch.util

import com.rumblesan.scalaglitch.util.RichBufferedImage._

import scodec.bits._

import scala.util.Random
import java.io.{ByteArrayInputStream}
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


object JpegGlitcher {

  def apply(image: BufferedImage, chance: Int): BufferedImage = {
    val bytes = image.getJpegBytes

    val sections = JpegParser.parse(bytes).map(s => {
      if (s.name == "DQT") {
        JpegGlitcher.glitchQuantTable(s, chance)
      } else {
        s
      }
    })

    val dataout = sections.foldLeft(ByteVector.empty)((out, sect) => {
      out ++ JpegParser.writeData(sect)
    })

    ImageIO.read(new ByteArrayInputStream(dataout.toArray))

  }

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

