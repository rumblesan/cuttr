package com.rumblesan.scalaglitch.util

import scodec.bits._

object JpegParser {

  val parsers: List[JpegParser] = List(
    new JpegSOI(),
    new JpegSOF0(),
    new JpegSOF2(),
    new JpegDHT(),
    new JpegDQT(),
    new JpegSOS(),
    new JpegAPP(),
    new JpegCOM(),
    new JpegEOI()
  )

  def parse(data: ByteVector): List[JpegStructureData] = {
    if (data.isEmpty) {
      Nil
    } else {
      val tag = data.take(2)
      getParser(tag).map(p => p.readData(data)) match {
        case Some((s, remaining)) => s :: parse(remaining)
        case None => Nil
      }
    }
  }

  def getParser(tag: ByteVector): Option[JpegParser] = {
    parsers.find(p => p.parse(tag))
  }

  def writeData(jpegData: JpegStructureData): ByteVector = {
    jpegData.tag ++ jpegData.data
  }

}

sealed trait JpegParser {

  val name: String

  val tag: ByteVector

  def parse(tagVal: ByteVector): Boolean = {
    tagVal.toInt() == tag.toInt()
  }

  def readData(data: ByteVector): (JpegStructureData, ByteVector) = {

    val tagdata = data.take(2)
    val size = data.drop(2).take(2).toInt()
    val structData = data.drop(2).take(size) // include size data in data
    val remainder = data.drop(2).drop(size)

    (JpegStructureData(name, tagdata, structData), remainder)

  }

}

class JpegSOI extends JpegParser {

  val name = "SOI"

  val tag: ByteVector = ByteVector(List(0xFF, 0xD8).map(_.toByte))

  override def readData(data: ByteVector): (JpegStructureData, ByteVector) = {
    // this is just the start byte so just remove the tag
    val tagdata = data.take(2)
    val remainder = data.drop(2)
    (JpegStructureData(name, tagdata, ByteVector.empty), remainder)
  }

}

class JpegSOF0 extends JpegParser {
  val name = "SOF0"
  val tag: ByteVector = ByteVector(List(0xFF, 0xC0).map(_.toByte))
}

class JpegSOF2 extends JpegParser {
  val name = "SOF2"
  val tag: ByteVector = ByteVector(List(0xFF, 0xC2).map(_.toByte))
}

class JpegDHT extends JpegParser {
  val name = "DHT"
  val tag: ByteVector = ByteVector(List(0xFF, 0xC4).map(_.toByte))
}

class JpegDQT extends JpegParser {
  val name = "DQT"
  val tag: ByteVector = ByteVector(List(0xFF, 0xDB).map(_.toByte))
}

class JpegSOS extends JpegParser {
  val name = "SOS"
  val tag: ByteVector = ByteVector(List(0xFF, 0xDA).map(_.toByte))
  override def readData(data: ByteVector): (JpegStructureData, ByteVector) = {

    val tagdata = data.take(2)
    val headerSize = data.drop(2).take(2).toInt()
    val headerData = data.drop(2).take(headerSize) // include size data in data
    val remainder = data.drop(2).drop(headerSize)
    var last: ByteVector = remainder.take(1)

    val ff = ByteVector(List(0xFF.toByte))
    val zz = ByteVector(List(0x00.toByte))

    def recurToEnd(data: ByteVector, depth: Int): Int = {
      val d = data.take(1)
      val n = data.drop(1).take(1)
      if (d == ff && n != zz) {
        depth
      } else {
        recurToEnd(data.drop(1), depth + 1)
      }
    }

    val depth = recurToEnd(remainder, 1)
    val restOfData = remainder.take(depth)
    val realRemainder = remainder.drop(depth)

    (JpegStructureData(name, tagdata, headerData ++ restOfData), realRemainder)

  }
}

class JpegAPP extends JpegParser {
  val name = "SOS"
  val tag: ByteVector = ByteVector(List(0xFF, 0xE0).map(_.toByte))
  val tagUpper: ByteVector = ByteVector(List(0xFF, 0xEF).map(_.toByte))
  override def parse(tagVal: ByteVector): Boolean = {
    (tagVal.toInt() >= tag.toInt()) && (tagVal.toInt() <= tagUpper.toInt())
  }

}

class JpegCOM extends JpegParser {
  val name = "COM"
  val tag: ByteVector = ByteVector(List(0xFF, 0xFE).map(_.toByte))
}

class JpegEOI extends JpegParser {
  val name = "EOI"
  val tag: ByteVector = ByteVector(List(0xFF, 0xD9).map(_.toByte))

  override def readData(data: ByteVector): (JpegStructureData, ByteVector) = {
    // this is just the start byte so just remove the tag
    val tagdata = data.take(2)
    val remainder = data.drop(2)
    (JpegStructureData(name, tagdata, ByteVector.empty), remainder)
  }

}


case class JpegStructureData(name: String, tag: ByteVector, data: ByteVector)


