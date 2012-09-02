package com.rumblesan.cuttr

import Numeric._

object PixelTransform {

  def funcVar[T](width:Int, height:Int, xFunc:(Int => T), yFunc:(Int => T))(implicit n:Numeric[T]) = {
    (x:Int, y:Int) => {
      (inRange(width,  xFunc(x)),
       inRange(height, yFunc(y)))
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

