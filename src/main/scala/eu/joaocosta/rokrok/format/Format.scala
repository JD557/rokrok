package eu.joaocosta.rokrok.format

import java.io.ByteArrayInputStream
import java.io.InputStream
import scala.util.Try

import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.Request

trait Format:
  def parseDocument(inputStream: InputStream, requestContext: Request): Try[Document]

  def parseDocument(textData: IterableOnce[String], requestContext: Request): Try[Document] =
    parseDocument(ByteArrayInputStream(textData.iterator.mkString("\r\n").getBytes()), requestContext)
