package com.rumblesan.cuttr

object PixelTransform {

  def funcVar(width:Int, height:Int, xFunc:(Int => Int), yFunc:(Int => Int)) = {
    (x:Int, y:Int) => {
      (inRange(0, width,  xFunc(x)),
       inRange(0, height, yFunc(y)))
    }
  }

  def shift(width:Int, height:Int, xShift:Int, yShift:Int) = {

    (x:Int, y:Int) => {
      (inRange(0, width,  x + xShift),
       inRange(0, height, y + yShift))
    }

  }

  def inRange(min:Int, max:Int, value:Int) = {
    val range = (max + 1) - min
    val wrapped = (value - min) % range
    if (wrapped < 0) {
      (wrapped + max) + 1
    } else {
      wrapped + min
    }
  }

}

