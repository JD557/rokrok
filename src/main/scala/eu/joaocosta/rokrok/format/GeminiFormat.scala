package eu.joaocosta.rokrok.format

import scala.util.Try

import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.Document.Element
import eu.joaocosta.rokrok.Request

object GeminiFormat extends Format:
  def parseDocument(lines: IterableOnce[String], requestContext: Request): Try[Document] =
    Try:
      val items: List[GeminiItem] = lines.iterator
        .foldLeft((false, List.empty[GeminiItem])) { case ((monospace, acc), str) =>
          val rawItem = GeminiItem.parse(str)

          if (rawItem.itemType == "```")
            (!monospace, acc)
          else if (monospace) (monospace, rawItem.copy(itemType = "```") :: acc)
          else (monospace, rawItem :: acc)
        }
        ._2
        .reverse

      Document(items.map: item =>
        item.itemType match
          case "" =>
            Element.Text(item.userString)
          case "#" =>
            Element.Heading(item.userString)
          case "```" =>
            Element.MonospaceText(item.userString)
          case "=>" =>
            val url = item.url match
              case "" | "/" =>
                s"${requestContext.baseUrl}/"
              case u if !u.contains("://") =>
                s"${requestContext.baseUrl}/$u"
              case u => u
            Element.Link(item.userString, url)
          case _ =>
            Element.Other(item.userString, "OTHER"))

  final case class GeminiItem(itemType: String, userString: String, url: String)

  object GeminiItem:
    def parse(str: String): GeminiItem =
      if (str.startsWith("```")) // Monospace switch
        GeminiItem("```", "", "")
      else if (str.startsWith("=>")) // Link
        val raw  = str.drop(2).trim.split("[ \t]")
        val url  = raw.headOption.getOrElse("/")
        val text =
          if (raw.size >= 2) raw.drop(1).mkString(" ")
          else url
        val item = GeminiItem(
          itemType = "=>",
          userString = text,
          url = url
        )
        item
      else if (str.startsWith("#")) // Heading
        GeminiItem(
          itemType = "#",
          userString = str,
          url = ""
        )
      else
        GeminiItem(
          itemType = "",
          userString = str,
          url = ""
        )
