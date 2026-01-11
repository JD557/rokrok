package eu.joaocosta.rokrok.format

import java.io.*
import scala.io.*
import scala.util.Try
import scala.util.Using

import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.Document.Element
import eu.joaocosta.rokrok.Request

object GeminiFormat extends Format:
  def parseDocument(inputStream: InputStream, requestContext: Request): Try[Document] =
    Using.Manager: use =>
      val items =
        use(Source.fromInputStream(inputStream)(using Codec.UTF8)).getLines().map(str => GeminiItem.parse(str)).toList

      Document(
        items.map: item =>
          item.itemType match
            case "" =>
              Element.Text(item.userString)
            case "=>" =>
              val url = item.url match
                case ""|"/" =>
                  s"${requestContext.baseUrl}/"
                case u if !u.contains("://") =>
                  s"${requestContext.baseUrl}/$u"
                case u => u
              Element.Link(item.userString, url)
            case _ =>
              Element.Other(item.userString, "OTHER")
      )

  final case class GeminiItem(itemType: String, userString: String, url: String)

  object GeminiItem:
    def parse(str: String): GeminiItem =
      if (str.startsWith("=>")) // LINK
        val raw  = str.drop(2).trim.split("[ \t]")
        val url  = raw.headOption.getOrElse("/")
        val item = GeminiItem(
          itemType = "=>",
          userString = raw.drop(1).mkString(" "),
          url = url
        )
        item
      else
        GeminiItem(
          itemType = "",
          userString = str,
          url = ""
        )
