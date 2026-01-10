package eu.joaocosta.rokrok.client

import scala.concurrent.*

import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.rokrok.Document

trait Client:
  def requestDocument(selector: String, hostname: String, port: Int)(using ExecutionContext): Future[Document]
  def requestPlainText(selector: String, hostname: String, port: Int)(using ExecutionContext): Future[String]
  def requestImage(selector: String, hostname: String, port: Int)(using ExecutionContext): Future[RamSurface]
