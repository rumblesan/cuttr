package com.rumblesan.scala_images

import org.specs2.mutable._

class PixelTransformSpec extends Specification {

  "The 'PixelTransform' object inRange method" should {
    "return an integer unchanged if it's within the range" in {
      PixelTransform.inRange(5, 10, 5) must_== 5
      PixelTransform.inRange(0, 10, 5) must_== 5
      PixelTransform.inRange(-5, 5, 0) must_== 0
      PixelTransform.inRange(-5, 5, -3) must_== -3
      PixelTransform.inRange(-5, -1, -3) must_== -3
    }
    "return an integer unchanged if it equals the maximum value" in {
      PixelTransform.inRange(5, 10, 10) must_== 10
      PixelTransform.inRange(-5, 0, 0) must_== 0
      PixelTransform.inRange(-10, -5, -5) must_== -5
    }
    "return an integer unchanged if it equals the minimum value" in {
      PixelTransform.inRange(5, 10, 5) must_== 5
      PixelTransform.inRange(-5, 0, -5) must_== -5
      PixelTransform.inRange(-10, -5, -10) must_== -10
    }
    "return an integer wrapped to above the min if it's above the max" in {
      PixelTransform.inRange(1, 10, 15) must_== 5
      PixelTransform.inRange(5, 10, 15) must_== 9
      PixelTransform.inRange(5, 10, 11) must_== 5
      PixelTransform.inRange(-5, 5, 6) must_== -5
      PixelTransform.inRange(-5, 0, 5) must_== -1
      PixelTransform.inRange(-5, -1, 3) must_== -2
    }
    "return an integer wrapped to below the max if it's below the min" in {
      PixelTransform.inRange(0, 10, -5) must_== 6
      PixelTransform.inRange(5, 10, 4) must_== 10
      PixelTransform.inRange(-5, 0, -8) must_== -2
      PixelTransform.inRange(-15, -7, -20) must_== -11
      PixelTransform.inRange(-15, -7, -34) must_== -7
    }
  }
}
