package eu.joaocosta.rokrok

final case class Document(elements: List[Document.Element])

object Document:
  def empty = Document(Nil)

  def fromStrings(strs: IterableOnce[String]): Document =
    if (strs.iterator.isEmpty) Document.empty
    else Document(strs.iterator.toList.map(s => Element.Text(s)))

  def fromString(str: String): Document =
    fromStrings(str.split("\n"))

  enum Element:
    case Text(content: String)
    case Error(description: String)
    case Link(description: String, url: String)
    case Input(description: String, url: String)
    case Image(description: String, url: String)
    case File(description: String, url: String, datatype: String)
    case Other(description: String, datatype: String)
