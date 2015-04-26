package com.rumblesan.scalaglitch.util

import com.rumblesan.scalaglitch.types._

object CoordWrapper extends GlitchTypes {

  def wrapCoords(width: Int, height: Int, xFunc:(Int => Int), yFunc:(Int => Int)): Coord => Coord = {
    (coords: Coord) => {
      (inRange(width,  xFunc(coords._1)),
       inRange(height, yFunc(coords._2)))
    }
  }

  def inRange(max: Int, value: Int): Int = {
    if (value > max) {
      value - max
    } else if (value < 0) {
      value + max
    } else {
      value
    }
  }

}

