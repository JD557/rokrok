package eu.joaocosta.rokrok.state

import scala.concurrent.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.*

import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.client.Client
import eu.joaocosta.rokrok.client.gopher.*
import eu.joaocosta.rokrok.client.http.*
import eu.joaocosta.rokrok.state.Page.PageType

/** Currently shown page
  */
final case class Page(
    query: String = "localhost",
    content: Future[Either[RamSurface, Document]] = Future.successful(Right(Page.defaultHomepage)),
    searchInput: Option[String] = None,
    history: List[String] = Nil,
    offset: Int = 0
):
  val Page.ParsedQuery(client, host, port, pageType, selector) = Page.parseQuery(query)

  /** Loads the specified URL, trying to autodetect the format */
  def load(): Page =
    pageType match
      case PageType.Document    => loadPage()
      case PageType.PlainText => loadText()
      case PageType.Image     => loadBitmap()

  /** Returns to the initial state */
  def loadHome(): Page = Page()

  /** Returns to the previous page */
  def goBack(): Page = history match
    case curr :: prev :: remainingHistory =>
      copy(
        query = prev,
        searchInput = None,
        history = remainingHistory
      ).load()
    case _ => loadHome()

  /** Loads the page specified */
  def loadPage(): Page =
    copy(
      content = client.requestDocument(selector, host, port).map(Right.apply),
      history = query :: history,
      offset = 0
    )

  /** Loads the specified raw text file */
  def loadText(): Page =
    copy(
      content = client.requestPlainText(selector, host, port).map(Document.fromString).map(Right.apply),
      history = query :: history,
      offset = 0
    )

  /** Loads the specified raw bitmap file */
  def loadBitmap(): Page =
    copy(
      content = client.requestImage(selector, host, port).map(Left.apply),
      history = query :: history,
      offset = 0
    )

  /** Loads the nextpage with the search query */
  def performSearch(): Page =
    copy(
      query = query + searchInput.map(search => s"\t$search").getOrElse(""),
      history = query :: history,
      searchInput = None
    ).loadPage()

  /** Text content ignoring errors and binary files */
  val textContent: List[Document.Element] =
    content.value.flatMap(_.toOption).flatMap(_.toOption).map(_.elements).getOrElse(Nil)

  /** Image content, ignoring errors and text files */
  val imageContent: Option[RamSurface] = content.value.flatMap(_.toOption).flatMap(_.left.toOption)

  /** Error message, populated if the page loading failed */
  def errorMessage: Option[String] =
    content.failed.value
      .flatMap(_.toOption)
      .map(_.getMessage)

object Page:
  val defaultHomepage =
    Using.Manager { use =>
      val is = use(this.getClass().getResourceAsStream("/homepage.txt"))
      GopherItem.toDocument(GopherItem.parse(is).get)
    }.get

  enum PageType:
    case Document
    case PlainText
    case Image

  final case class ParsedQuery(client: Client, host: String, port: Int, pageType: PageType, selector: String)

  def parseQuery(query: String): ParsedQuery =
    val (client: Client, defaultPort: Int, baseQuery: String) =
      if (query.startsWith("gopher://")) (GopherClient, 70, query.drop(9))
      else if (query.startsWith("http://")) (HttpClient, 80, query.drop(7))
      else (GopherClient, 70, query)

    val (host: String, port: Int, path: String) = baseQuery match
      case s"$host:$port/$path" => (host, port.toIntOption.getOrElse(defaultPort), "/" + path)
      case s"$host/$path"       => (host, 70, "/" + path)
      case s"$host:$port"       => (host, port.toIntOption.getOrElse(defaultPort), "/")
      case host                 => (host, 70, "/")

    client match
      case GopherClient =>
        val (itemType: Char, selector: String) = path match
          case s"/$itemType/$selector" if itemType.size == 1 =>
            (itemType.head, "/" + selector)
          case selector => ('1', selector)

        val pageType = itemType match
          case '0'                   => PageType.PlainText
          case 'I' | ':' | '9' | 'p' => PageType.Image
          case _                     => PageType.Document

        ParsedQuery(client, host, port, pageType, selector)

      case HttpClient =>
        val pageType =
          if (path.endsWith(".txt")) PageType.PlainText
          else if (path.endsWith(".bmp")) PageType.Image
          else PageType.Document
        ParsedQuery(client, host, port, pageType, path)
