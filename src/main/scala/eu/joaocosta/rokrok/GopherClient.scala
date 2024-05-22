package eu.joaocosta.rokrok

import java.io.*
import java.net.*
import scala.util.Try
import scala.util.Using
import scala.io.*
import scala.concurrent.*

import eu.joaocosta.minart.graphics.image.bmp.BmpImageFormat
import eu.joaocosta.minart.graphics.RamSurface

object GopherClient:
  final case class GopherItem(itemType: Char, userString: String, selector: String, hostname: String, port: Int)
  object GopherItem:
    def parse(str: String): GopherItem =
      val raw = str.split("\t")
      GopherItem(
        itemType = raw.applyOrElse(0, _ => "").headOption.getOrElse('1'),
        userString = raw.applyOrElse(0, _ => "").drop(1),
        selector = raw.applyOrElse(1, _ => ""),
        hostname = raw.applyOrElse(2, _ => ""),
        port = raw.applyOrElse(3, _ => "").toIntOption.getOrElse(1)
      )

    def info(str: String): GopherItem =
      GopherItem(
        itemType = 'i',
        userString = str,
        selector = "/",
        hostname = "error.host",
        port = 1
      )

    def parse(inputStream: InputStream): Try[List[GopherItem]] = Using.Manager: use =>
      use(Source.fromInputStream(inputStream)(Codec.UTF8)).getLines().map(str => GopherItem.parse(str)).toList

  def request(selector: String, hostname: String, port: Int): Try[List[GopherItem]] =
    Using.Manager: use =>
      val socket = new Socket(hostname, port)
      socket.setSoTimeout(5000)

      val in  = use(socket.getInputStream())
      val out = use(socket.getOutputStream())

      println("Perfoming request")
      out.write(s"$selector\r\n".getBytes())
      out.flush()

      println("Waiting response")
      GopherItem.parse(in).get

  def requestAsync(selector: String, hostname: String, port: Int)(implicit
      ec: ExecutionContext
  ): Future[List[GopherItem]] =
    Future(blocking(request(selector, hostname, port).get))

  def requestText(selector: String, hostname: String, port: Int): Try[List[GopherItem]] =
    Using.Manager: use =>
      val socket = new Socket(hostname, port)
      socket.setSoTimeout(5000)

      val in  = use(socket.getInputStream())
      val out = use(socket.getOutputStream())

      println("Perfoming request")
      out.write(s"$selector\r\n".getBytes())
      out.flush()

      println("Waiting response")
      use(Source.fromInputStream(in)(Codec.UTF8)).getLines().map(str => GopherItem.info(str)).toList

  def requestTextAsync(selector: String, hostname: String, port: Int)(implicit
      ec: ExecutionContext
  ): Future[List[GopherItem]] =
    Future(blocking(requestText(selector, hostname, port).get))

  def requestBmp(selector: String, hostname: String, port: Int): Try[RamSurface] =
    val result = Using.Manager: use =>
      val socket = new Socket(hostname, port)
      socket.setSoTimeout(5000)

      val in  = use(socket.getInputStream())
      val out = use(socket.getOutputStream())

      println("Perfoming request")
      out.write(s"$selector\r\n".getBytes())
      out.flush()

      println("Waiting response")
      BmpImageFormat.defaultFormat.loadImage(in).left.map(message => new Exception(message)).toTry
    result.flatten

  def requestBmpAsync(selector: String, hostname: String, port: Int)(implicit
      ec: ExecutionContext
  ): Future[RamSurface] =
    Future(blocking(requestBmp(selector, hostname, port).get))
