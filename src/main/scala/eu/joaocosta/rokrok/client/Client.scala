package eu.joaocosta.rokrok.client

import scala.concurrent.*

import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.Request
import eu.joaocosta.rokrok.format.Format

trait Client:
  def protocol: String
  def defaultPort: Int
  def requestDocument(request: Request, format: Format)(using
      ExecutionContext
  ): Future[Document]
  def requestImage(request: Request)(using ExecutionContext): Future[RamSurface]
