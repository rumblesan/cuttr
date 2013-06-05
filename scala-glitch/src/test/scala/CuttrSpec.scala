package com.rumblesan.scalaglitch

import org.specs2.mutable._

import javax.imageio.ImageIO



class CuttrSpec extends Specification {

  "The Cuttr class" should {

    "be instantiated correctly with basic args" in {

      val image = ImageIO.read(getClass.getResource("/landscape.jpeg"))

      val cuttr = new Cuttr(image)

      cuttr must haveClass[Cuttr]

    }

  }

}
