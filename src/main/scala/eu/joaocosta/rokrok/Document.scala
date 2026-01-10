package eu.joaocosta.rokrok

final case class Document(elements: List[Document.Element])

object Document:
  def empty = Document(Nil)

  def fromString(str: String): Document =
    if (str.isEmpty) Document.empty
    else Document(str.split("\n").toList.map(s => Element.Text(s)))

  enum Element:
    case Text(content: String)
    case Error(description: String)
    case Link(description: String, url: String)
    case Input(description: String, url: String)
    case Image(description: String, url: String)
    case File(description: String, url: String, datatype: String)
    case Other(description: String, datatype: String)
