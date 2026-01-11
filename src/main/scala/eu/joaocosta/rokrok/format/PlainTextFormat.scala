package eu.joaocosta.rokrok.format

import java.io.*
import scala.io.*
import scala.util.Try
import scala.util.Using

import eu.joaocosta.rokrok.Document

object PlainTextFormat extends Format:
  def parseDocument(inputStream: InputStream): Try[Document] =
    Using.Manager: use =>
      Document.fromStrings(use(Source.fromInputStream(inputStream)(using Codec.UTF8)).getLines())
