package eu.joaocosta.rokrok.client.gopher

import java.io.*
import scala.io.*
import scala.util.Try
import scala.util.Using

import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.Document.Element

final case class GopherItem(itemType: Char, userString: String, selector: String, hostname: String, port: Int):
  lazy val targetUrl = s"${hostname}:${port}/${itemType}${selector}"

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

  def parse(inputStream: InputStream): Try[List[GopherItem]] = Using.Manager: use =>
    use(Source.fromInputStream(inputStream)(using Codec.UTF8)).getLines().map(str => GopherItem.parse(str)).toList

  def toDocument(items: List[GopherItem]): Document =
    Document(
      items.map: item =>
        item.itemType match
          case 'i' =>
            Element.Text(item.userString)
          case '0' =>
            Element.File(item.userString, item.targetUrl, "TEXT")
          case '1' | '+' =>
            Element.Link(item.userString, item.targetUrl)
          case '2' =>
            Element.Other(item.userString, "CCSO")
          case '3' =>
            Element.Error(item.userString)
          case '4' =>
            Element.Other(item.userString, "BINEX")
          case '5' =>
            Element.Other(item.userString, "DOS")
          case '6' =>
            Element.Other(item.userString, "UUENC")
          case '7' =>
            Element.Input(item.userString, item.targetUrl)
          case '8' | 'T' =>
            Element.Other(item.userString, "TELNET")
          case '9' =>
            Element.Other(item.userString, "BINARY")
          case 'I' | ':' | 'p' | 'g' =>
            Element.Image(item.userString, item.targetUrl)
          case ';' =>
            Element.Other(item.userString, "IMAGE")
          case '<' | 's' =>
            Element.Other(item.userString, "SOUND")
          case 'd' | 'h' | 'r' | 'P' | 'x' =>
            Element.Other(item.userString, "DOCUMENT")
          case _ =>
            Element.Other(item.userString, "OTHER")
    )

  def info(str: String): GopherItem =
    GopherItem(
      itemType = 'i',
      userString = str,
      selector = "/",
      hostname = "error.host",
      port = 1
    )
