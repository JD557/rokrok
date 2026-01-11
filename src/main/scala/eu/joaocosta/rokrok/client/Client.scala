package eu.joaocosta.rokrok.client

import scala.concurrent.*

import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.format.Format

trait Client:
  def protocol: String
  def defaultPort: Int
  def requestDocument(format: Format, selector: String, hostname: String, port: Int)(using
      ExecutionContext
  ): Future[Document]
  def requestImage(selector: String, hostname: String, port: Int)(using ExecutionContext): Future[RamSurface]
