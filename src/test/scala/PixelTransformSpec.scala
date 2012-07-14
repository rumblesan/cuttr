package com.rumblesan.scala_images

import org.specs2.mutable._

class PixelTransformSpec extends Specification {

  "The 'PixelTransform' object inRange method" should {
    "return an integer unchanged if it's within the range" in {
      PixelTransform.inRange(0, 10, 5) must_== 5
    }
    "return an integer minus max if it's above the range" in {
      PixelTransform.inRange(0, 10, 15) must_== 5
    }
    "return an integer plus max if it's below the range" in {
      PixelTransform.inRange(0, 10, -5) must_== 5
    }
  }
}
