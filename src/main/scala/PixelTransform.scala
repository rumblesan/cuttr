package com.rumblesan.scala_images

object PixelTransform {

  def shift(width:Int, height:Int, xShift:Int, yShift:Int) = {

    (x:Int, y:Int) => {
      val newX = if ((x + xShift) >= width) {
        (x + xShift) - width
      } else if ((x + xShift) < 0) {
        x + xShift + width
      } else {
        x + xShift
      }
      val newY = if ((y + yShift) >= height) {
        (y + yShift) - height
      } else if ((y + yShift) < 0) {
        y + yShift + height
      } else {
        y + yShift
      }
      (newX, newY)
    }

  }

}

