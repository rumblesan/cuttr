package com.rumblesan.scalaglitch.util

import java.io.{ ByteArrayOutputStream, FileOutputStream }

import javax.imageio.stream.MemoryCacheImageOutputStream
import javax.imageio.{IIOException, IIOImage, ImageIO, ImageTypeSpecifier, ImageWriter, ImageWriteParam}
import javax.imageio.metadata.{IIOMetadata, IIOMetadataNode}

import java.awt.image.{BufferedImage, RenderedImage}

import scala.collection.JavaConversions._

object GifWriter {

  def apply(
    images: List[BufferedImage],
    loopContinuously: Boolean,
    timeBetweenFramesMS: Int): Option[Array[Byte]] = {

    images match {
      case Nil => None
      case i => Some(writeImages(i, i.head.getType(), loopContinuously, timeBetweenFramesMS))
    }

  }

  def writeImages(
    images: List[BufferedImage],
    imageType: Int,
    loopContinuously: Boolean,
    timeBetweenFramesMS: Int): Array[Byte] = {

    val baos = new ByteArrayOutputStream()
    val ios = new MemoryCacheImageOutputStream(baos)
    val gifWriter: ImageWriter = getWriter()

    val imageTypeSpecifier: ImageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType)
    val imageWriteParam: ImageWriteParam = gifWriter.getDefaultWriteParam()
    val imageMetaData = generateMetaData(gifWriter, imageWriteParam, imageTypeSpecifier, loopContinuously, timeBetweenFramesMS)

    gifWriter.setOutput(ios)

    gifWriter.prepareWriteSequence(null)

    for (img <- images) {
      writeToSequence(gifWriter, img, imageMetaData, imageWriteParam)
    }

    ios.flush()
    gifWriter.endWriteSequence()

    val imageData = baos.toByteArray()
    ios.close()

    val outfile = new FileOutputStream("test.gif")
    outfile.write(imageData)
    outfile.close()

    imageData
  }

  def getWriter(): ImageWriter = {
    val iter = ImageIO.getImageWritersBySuffix("gif")
    if(!iter.hasNext()) {
      throw new IIOException("No GIF Image Writers Exist")
    } else {
      return iter.next()
    }
  }


  def getNode(rootNode: IIOMetadataNode, nodeName: String): IIOMetadataNode = {
    val nNodes: Int = rootNode.getLength()

    for (i <- 0 until nNodes) {
      if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0) {
        return rootNode.item(i).asInstanceOf[IIOMetadataNode]
      }
    }

    val node: IIOMetadataNode = new IIOMetadataNode(nodeName)
    rootNode.appendChild(node)
    return node
  }

  def generateMetaData(gifWriter: ImageWriter, imageWriteParam: ImageWriteParam, imageTypeSpecifier: ImageTypeSpecifier, loopContinuously: Boolean, timeBetweenFramesMS: Int): IIOMetadata = {

    val imageMetaData: IIOMetadata = gifWriter.getDefaultImageMetadata(
      imageTypeSpecifier,
      imageWriteParam
    )

    val metaFormatName: String = imageMetaData.getNativeMetadataFormatName()

    val root: IIOMetadataNode = imageMetaData.getAsTree(metaFormatName).asInstanceOf[IIOMetadataNode]

    val appEntensionsNode: IIOMetadataNode = getNode(
      root,
      "ApplicationExtensions"
    )

    val graphicsControlExtensionNode: IIOMetadataNode = getNode(
      root,
      "GraphicControlExtension"
    )

    graphicsControlExtensionNode.setAttribute("disposalMethod", "none")
    graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE")
    graphicsControlExtensionNode.setAttribute(
      "transparentColorFlag",
      "FALSE"
    )
    graphicsControlExtensionNode.setAttribute(
      "delayTime",
      Integer.toString(timeBetweenFramesMS / 10)
    )
    graphicsControlExtensionNode.setAttribute(
      "transparentColorIndex",
      "0"
    )

    val commentsNode: IIOMetadataNode = getNode(root, "CommentExtensions")
    commentsNode.setAttribute("CommentExtension", "Created by MAH")

    val child: IIOMetadataNode = new IIOMetadataNode("ApplicationExtension")
    child.setAttribute("applicationID", "NETSCAPE")
    child.setAttribute("authenticationCode", "2.0")

    val loop: Int = if (loopContinuously) 0 else 1

    child.setUserObject(
      Array( 0x1.toByte, (loop & 0xFF).toByte, ((loop >> 8) & 0xFF).toByte )
    )

    appEntensionsNode.appendChild(child)

    imageMetaData.setFromTree(metaFormatName, root)

    imageMetaData

  }

  def writeToSequence(gifWriter: ImageWriter, img: RenderedImage, imageMetaData: IIOMetadata, imageWriteParam: ImageWriteParam): Unit = {
    gifWriter.writeToSequence(
      new IIOImage(
        img,
        null,
        imageMetaData
      ),
      imageWriteParam
    )
  }

}

