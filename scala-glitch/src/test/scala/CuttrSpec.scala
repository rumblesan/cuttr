package com.rumblesan.scalaglitch

import org.specs2.mutable._

import javax.imageio.ImageIO
import java.io.{File, IOException}




class CuttrSpec extends Specification {

  "The Cuttr class" should {

    "be instantiated correctly with basic args" in {

      val image = ImageIO.read(getClass.getResource("/landscape.jpeg"))

      val cuttr = new Cuttr(image)

      cuttr must haveClass[Cuttr]

    }

    "can glitch an image" in {

      val image = ImageIO.read(getClass.getResource("/landscape.jpeg"))

      val cuttr = new Cuttr(image)

      val outfile = new File("output.png")

      ImageIO.write(cuttr.glitch(), "png", outfile)

      cuttr must haveClass[Cuttr]

    }

  }

}
