package eu.joaocosta.gopher

import java.io.*
import java.net.*
import scala.io.Source
import scala.util.Try
import scala.util.Using

object GopherClient:
  final case class GopherItem(itemType: Char, userString: String, selector: String, hostname: String, port: Int)
  object GopherItem:
    def parse(str: String): GopherItem =
      val raw = str.split("\t")
      GopherItem(
        itemType = raw.applyOrElse(0, _ => "").headOption.getOrElse('i'),
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


  def request(selector: String, hostname: String, port: Int): Try[List[GopherItem]] =
    Using.Manager: use =>
      val socket = new Socket(hostname, port)

      val in  = use(socket.getInputStream())
      val out = use(socket.getOutputStream())

      println("Perfoming request")
      out.write(s"$selector\r\n".getBytes())
      out.flush()

      println("Waiting response")
      use(Source.fromInputStream(in)).getLines().map(str => GopherItem.parse(str)).toList

  def requestText(selector: String, hostname: String, port: Int): Try[List[GopherItem]] =
    Using.Manager: use =>
      val socket = new Socket(hostname, port)

      val in  = use(socket.getInputStream())
      val out = use(socket.getOutputStream())

      println("Perfoming request")
      out.write(s"$selector\r\n".getBytes())
      out.flush()

      println("Waiting response")
      use(Source.fromInputStream(in)).getLines().map(str => GopherItem.info(str)).toList
