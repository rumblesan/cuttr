package com.rumblesan.scalaglitch.util

import scala.util.Random


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

