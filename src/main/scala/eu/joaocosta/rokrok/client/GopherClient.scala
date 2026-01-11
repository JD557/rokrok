package eu.joaocosta.rokrok.client

import java.net.*
import scala.concurrent.*
import scala.util.Using

import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.minart.graphics.image.bmp.BmpImageFormat
import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.Request
import eu.joaocosta.rokrok.client.Client
import eu.joaocosta.rokrok.format.*

object GopherClient extends Client:
  def protocol: String = "gopher://"

  def defaultPort: Int = 70

  def extractItemTypeAndSelector(request: Request): (Char, String) =
    request.path match
      case s"/$itemType/$selector" if itemType.size == 1 =>
        (itemType.head, "/" + selector)
      case selector => ('1', selector)

  def requestDocument(request: Request, format: Format)(using
      ExecutionContext
  ): Future[Document] =
    Future.apply:
      blocking:
        Using.Manager { use =>
          val socket = new Socket(request.host, request.port)
          socket.setSoTimeout(5000)

          val in  = use(socket.getInputStream())
          val out = use(socket.getOutputStream())

          val selector = extractItemTypeAndSelector(request)._2

          out.write(s"$selector\r\n".getBytes())
          out.flush()

          format.parseDocument(in, request).get
        }.get

  def requestImage(request: Request)(using ExecutionContext): Future[RamSurface] =
    Future.apply:
      blocking:
        Using
          .Manager { use =>
            val socket = new Socket(request.host, request.port)
            socket.setSoTimeout(5000)

            val in  = use(socket.getInputStream())
            val out = use(socket.getOutputStream())

            val selector = extractItemTypeAndSelector(request)._2

            out.write(s"$selector\r\n".getBytes())
            out.flush()

            BmpImageFormat.defaultFormat.loadImage(in).left.map(message => new Exception(message)).toTry
          }
          .flatten
          .get
