package eu.joaocosta.rokrok.client

import java.net.*
import scala.concurrent.*
import scala.io.*
import scala.util.Using

import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.client.Client
import eu.joaocosta.rokrok.format.Format

object HttpClient extends Client:
  def protocol: String = "http://"

  def defaultPort: Int = 80

  def requestDocument(format: Format, selector: String, hostname: String, port: Int)(using
      ExecutionContext
  ): Future[Document] =
    Future.apply:
      blocking:
        Using.Manager { use =>
          val socket = new Socket(hostname, port)
          socket.setSoTimeout(5000)

          val in  = use(socket.getInputStream())
          val out = use(socket.getOutputStream())

          out.write(s"GET $selector HTTP/1.0\r\n".getBytes())
          out.write(s"Host: $hostname\r\n".getBytes())
          out.write("\r\n".getBytes())
          out.flush()

          format.parseDocument(use(Source.fromInputStream(in)(using Codec.UTF8)).getLines().dropWhile(_ != "")).get
        }.get

  def requestImage(selector: String, hostname: String, port: Int)(using ExecutionContext): Future[RamSurface] =
    Future.failed(new Exception("Images are not supported via HTTP"))
