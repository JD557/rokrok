package eu.joaocosta.rokrok.state

import scala.concurrent.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.*
import scala.util.*

import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.rokrok.Document
import eu.joaocosta.rokrok.Request
import eu.joaocosta.rokrok.client.*
import eu.joaocosta.rokrok.format.*
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
  val Page.ParsedQuery(request @ Request(client, host, port, path), pageType, selector) =
    Page.parseQuery(query)

  /** Sets a URL */
  def setUrl(url: String): Page =
    copy(query = url)

  /** Loads the specified URL, trying to autodetect the format */
  def load(): Page =
    pageType match
      case PageType.Document(format) => loadPage(format)
      case PageType.PlainText        => loadPage(PlainTextFormat)
      case PageType.Image            => loadBitmap()

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
  def loadPage(format: Format): Page =
    copy(
      content = client.requestDocument(request, format).map(Right.apply),
      history = query :: history,
      offset = 0
    )

  /** Loads the specified raw bitmap file */
  def loadBitmap(): Page =
    copy(
      content = client.requestImage(request).map(Left.apply),
      history = query :: history,
      offset = 0
    )

  /** Loads the nextpage with the search query */
  def performSearch(): Page =
    copy(
      query = query + searchInput.map(search => s"\t$search").getOrElse(""),
      history = query :: history,
      searchInput = None
    ).loadPage(GopherFormat)

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
  val defaultHomepage: Document =
    Using
      .Manager { use =>
        val source = use(Source.fromInputStream(this.getClass().getResourceAsStream("/homepage.txt")))
        GopherFormat.parseDocument(source.getLines(), Request.parse(""))
      }
      .flatten
      .get

  enum PageType:
    case Document(format: Format)
    case PlainText
    case Image

  final case class ParsedQuery(context: Request, pageType: PageType, selector: String)

  def parseQuery(query: String, defaultClient: Client = GopherClient): ParsedQuery =
    val request = Request.parse(query, defaultClient)

    request.client match
      case GopherClient =>
        val (itemType: Char, selector: String) = GopherClient.extractItemTypeAndSelector(request)

        val pageType = itemType match
          case '0'                   => PageType.PlainText
          case 'I' | ':' | '9' | 'p' => PageType.Image
          case _                     => PageType.Document(GopherFormat)

        ParsedQuery(request, pageType, selector)

      case HttpClient =>
        val pageType =
          if (request.path.endsWith(".txt")) PageType.PlainText
          else if (request.path.endsWith(".bmp")) PageType.Image
          else PageType.PlainText
        ParsedQuery(request, pageType, request.path)

      case SpartanClient =>
        ParsedQuery(request, PageType.Document(GeminiFormat), request.path)
