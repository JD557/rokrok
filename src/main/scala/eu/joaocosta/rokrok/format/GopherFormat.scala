package eu.joaocosta.rokrok.format

import scala.util.Try

import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.Document.Element
import eu.joaocosta.rokrok.Request

object GopherFormat extends Format:
  def parseDocument(lines: IterableOnce[String], requestContext: Request): Try[Document] =
    Try:
      val items = lines.iterator.map(str => GopherItem.parse(str)).toList

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

  final case class GopherItem(itemType: Char, userString: String, selector: String, hostname: String, port: Int):
    lazy val targetUrl =
      if (!hostname.contains("://")) // Inject Gopher protocol
        s"gopher://${hostname}:${port}/${itemType}${selector}"
      else if (hostname.startsWith("gopher://")) // Stay in Gopher
        s"${hostname}:${port}/${itemType}${selector}"
      else // Change protocol
        s"${hostname}:${port}/${selector}"

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
