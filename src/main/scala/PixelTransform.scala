package com.rumblesan.scala_images

object PixelTransform {

  def shift(width:Int, height:Int, xShift:Int, yShift:Int) = {

    (x:Int, y:Int) => {
      (inRange(0, width,  x + xShift),
        inRange(0, height, y + yShift))
    }

  }

  def inRange(min:Int, max:Int, value:Int) = {
    if (value >= max) {
      value - max
    } else if (value < min) {
      value + max
    } else {
      value
    }
  }

}

