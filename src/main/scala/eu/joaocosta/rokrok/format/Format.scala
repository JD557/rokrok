package eu.joaocosta.rokrok.format

import java.io.ByteArrayInputStream
import java.io.InputStream
import scala.util.Try

import eu.joaocosta.rokrok.Document

trait Format:
  def parseDocument(inputStream: InputStream): Try[Document]

  def parseDocument(textData: IterableOnce[String]): Try[Document] =
    parseDocument(ByteArrayInputStream(textData.iterator.mkString("\r\n").getBytes()))
