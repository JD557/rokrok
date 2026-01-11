package eu.joaocosta.rokrok.client

import java.net.*
import scala.concurrent.*
import scala.io.*
import scala.util.Using

import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.client.Client
import eu.joaocosta.rokrok.format.Format

object SpartanClient extends Client:
  def protocol: String = "spartan://"

  def defaultPort: Int = 300

  def requestDocument(format: Format, selector: String, hostname: String, port: Int)(using
      ExecutionContext
  ): Future[Document] =
    Future
      .apply {
        blocking:
          Using
            .Manager: use =>
              val socket = new Socket(hostname, port)
              socket.setSoTimeout(5000)

              val in  = use(socket.getInputStream())
              val out = use(socket.getOutputStream())

              out.write(s"$hostname $selector 0\r\n".getBytes())
              out.flush()

              use(Source.fromInputStream(in)(using Codec.UTF8)).getLines().toList
      }
      .map(_.get)
      .flatMap: data =>
        data.head match
          case s"2 $mime"     => Future.fromTry(format.parseDocument(data.tail))
          case s"3 $redirect" => Future.failed(new Exception("Spartan redirects are not supported"))
          case s"4 $error"    => Future.failed(new Exception(s"Client Error: $error"))
          case s"5 $error"    => Future.failed(new Exception(s"Server Error: $error"))
          case _              => Future.failed(new Exception("Unexpected server response"))

  def requestImage(selector: String, hostname: String, port: Int)(using ExecutionContext): Future[RamSurface] =
    Future.failed(new Exception("Images are not supported via Spartan"))
