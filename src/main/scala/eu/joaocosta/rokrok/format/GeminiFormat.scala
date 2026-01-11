package eu.joaocosta.rokrok.format

import java.io.*
import scala.io.*
import scala.util.Try
import scala.util.Using

import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.Document.Element

object GeminiFormat extends Format:
  def parseDocument(inputStream: InputStream): Try[Document] =
    Using.Manager: use =>
      val items =
        use(Source.fromInputStream(inputStream)(using Codec.UTF8)).getLines().map(str => GeminiItem.parse(str)).toList

      Document(
        items.map: item =>
          item.itemType match
            case "" =>
              Element.Text(item.userString)
            case "=>" =>
              Element.Link(item.userString, item.targetUrl)
            case _ =>
              Element.Other(item.userString, "OTHER")
      )

  final case class GeminiItem(itemType: String, userString: String, targetUrl: String)

  object GeminiItem:
    def parse(str: String): GeminiItem =
      if (str.startsWith("=>")) // LINK
        val raw    = str.drop(2).trim.split("[ \t]")
        val rawUrl = raw.headOption.getOrElse("/")
        val item   = GeminiItem(
          itemType = "=>",
          userString = raw.drop(1).mkString(" "),
          targetUrl = rawUrl
        )
        item
      else
        GeminiItem(
          itemType = "",
          userString = str,
          targetUrl = ""
        )
