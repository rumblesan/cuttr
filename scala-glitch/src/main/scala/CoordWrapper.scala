package com.rumblesan.scalaglitch

object CoordWrapper {

  def wrapCoords(width: Int, height: Int, xFunc:(Int => Int), yFunc:(Int => Int)): Pair[Int, Int] => Pair[Int, Int] = {
    (coords:Pair[Int,Int]) => {
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

