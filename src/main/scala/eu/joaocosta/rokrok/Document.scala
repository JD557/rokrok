package eu.joaocosta.rokrok

final case class Document(elements: List[Document.Element])

object Document:
  def empty = Document(Nil)

  def fromGopherItems(items: List[GopherClient.GopherItem]): Document =
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

  enum Element:
    case Text(content: String)
    case Error(description: String)
    case Link(description: String, url: String)
    case Input(description: String, url: String)
    case Image(description: String, url: String)
    case File(description: String, url: String, datatype: String)
    case Other(description: String, datatype: String)
