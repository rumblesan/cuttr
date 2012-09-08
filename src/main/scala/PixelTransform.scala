package com.rumblesan.cuttr.glitch

import Numeric._

object PixelTransform {

  implicit def PairDouble2PairInt(c:Pair[Double,Double]):Pair[Int,Int] = {
    (c._1.toInt, c._2.toInt)
  }
  
  def pixelTransform[T](width:Int, height:Int, xFunc:(Int => T), yFunc:(Int => T))(implicit n:Numeric[T]) = {
    (coords:Pair[Int,Int]) => {
      (inRange(width,  xFunc(coords._1)),
       inRange(height, yFunc(coords._2)))
    }
  }

  def inRange[T](max:Int, value:T)(implicit n:Numeric[T]) = {
    if (n.gteq(value, n.fromInt(max))) {
      n.minus(value, n.fromInt(max))
    } else if (n.lt(value, n.zero)) {
      n.plus(value, n.fromInt(max))
    } else {
      value
    }
  }

}

