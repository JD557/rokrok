package eu.joaocosta.rokrok.format

import scala.util.Try

import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.Request

trait Format:
  def parseDocument(lines: IterableOnce[String], requestContext: Request): Try[Document]
