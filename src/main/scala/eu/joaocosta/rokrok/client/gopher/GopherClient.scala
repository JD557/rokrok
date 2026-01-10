package eu.joaocosta.rokrok.client.gopher

import java.net.*
import scala.concurrent.*
import scala.io.*
import scala.util.Using

import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.minart.graphics.image.bmp.BmpImageFormat
import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.client.Client

object GopherClient extends Client:
  def requestDocument(selector: String, hostname: String, port: Int)(using ExecutionContext): Future[Document] =
    Future.apply:
      blocking:
        val res = Using.Manager: use =>
          val socket = new Socket(hostname, port)
          socket.setSoTimeout(5000)

          val in  = use(socket.getInputStream())
          val out = use(socket.getOutputStream())

          out.write(s"$selector\r\n".getBytes())
          out.flush()

          GopherItem.parse(in).get
        GopherItem.toDocument(res.get)

  def requestPlainText(selector: String, hostname: String, port: Int)(using ExecutionContext): Future[String] =
    Future.apply:
      blocking:
        Using.Manager { use =>
          val socket = new Socket(hostname, port)
          socket.setSoTimeout(5000)

          val in  = use(socket.getInputStream())
          val out = use(socket.getOutputStream())

          out.write(s"$selector\r\n".getBytes())
          out.flush()

          use(Source.fromInputStream(in)(using Codec.UTF8)).getLines().mkString("\n")
        }.get

  def requestImage(selector: String, hostname: String, port: Int)(using ExecutionContext): Future[RamSurface] =
    Future.apply:
      blocking:
        Using
          .Manager { use =>
            val socket = new Socket(hostname, port)
            socket.setSoTimeout(5000)

            val in  = use(socket.getInputStream())
            val out = use(socket.getOutputStream())

            out.write(s"$selector\r\n".getBytes())
            out.flush()

            BmpImageFormat.defaultFormat.loadImage(in).left.map(message => new Exception(message)).toTry
          }
          .flatten
          .get
