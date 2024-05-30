package eu.joaocosta.rokrok.state

import scala.concurrent.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.*

import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.rokrok.GopherClient

/** Currently shown page
  */
final case class Page(
    query: String = "localhost",
    content: Future[Either[RamSurface, List[GopherClient.GopherItem]]] = Future.successful(Right(Page.defaultHomepage)),
    searchInput: Option[String] = None,
    history: List[String] = Nil,
    offset: Int = 0
):
  val (host: String, port: Int, gopherPath: String) =
    val baseQuery = if (query.startsWith("gopher://")) query.drop(9) else query
    baseQuery match
      case s"$host:$port/$selector" => (host, port.toIntOption.getOrElse(70), "/" + selector)
      case s"$host/$selector"       => (host, 70, "/" + selector)
      case s"$host:$port"           => (host, port.toIntOption.getOrElse(70), "/")
      case host                     => (host, 70, "/")

  val (itemType: Char, selector: String) = gopherPath match
    case s"/$itemType/$selector" if itemType.size == 1 =>
      (itemType.head, "/" + selector)
    case selector => ('1', selector)

  /** Loads the specified URL, trying to autodetect the format */
  def load(): Page =
    itemType match
      case '0'                   => loadText()
      case 'I' | ':' | '9' | 'p' => loadBitmap()
      case _                     => loadPage()

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
      content = Future
        .fromTry(Try(port.toInt))
        .flatMap(port => GopherClient.requestAsync(selector, host, port).map(Right.apply)),
      history = query :: history,
      offset = 0
    )

  /** Loads the specified raw text file */
  def loadText(): Page =
    copy(
      content = Future
        .fromTry(Try(port.toInt))
        .flatMap(port => GopherClient.requestTextAsync(selector, host, port).map(Right.apply)),
      history = query :: history,
      offset = 0
    )

  /** Loads the specified raw bitmap file */
  def loadBitmap(): Page =
    copy(
      content = Future
        .fromTry(Try(port.toInt))
        .flatMap(port => GopherClient.requestBmpAsync(selector, host, port).map(Left.apply)),
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
  val textContent: List[GopherClient.GopherItem] = content.value.flatMap(_.toOption).flatMap(_.toOption).getOrElse(Nil)

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
      GopherClient.GopherItem.parse(is).get
    }.get
