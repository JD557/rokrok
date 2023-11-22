package eu.joaocosta.gopher

import scala.util.*
import eu.joaocosta.minart.graphics.RamSurface

/** Main application state
  */
final case class MainState(
    query: String = "localhost",
    pageContent: Try[Either[RamSurface, List[GopherClient.GopherItem]]] = Success(Right(MainState.defaultHomepage)),
    searchInput: Option[String] = None,
    offset: Int = 0
):
  val (host: String, port: Int, selector: String) =
    val baseQuery = if (query.startsWith("gopher://")) query.drop(9) else query
    baseQuery match
      case s"$host:$port/$selector" => (host, port.toIntOption.getOrElse(70), "/" + selector)
      case s"$host/$selector"       => (host, 70, "/" + selector)
      case s"$host:$port"           => (host, port.toIntOption.getOrElse(70), "/")
      case host                     => (host, 70, "/")

  /** Returns to the initial state */
  def loadHome(): MainState = MainState()

  /** Loads the page specified */
  def loadPage(): MainState =
    copy(
      pageContent = Try(port.toInt).flatMap(port => GopherClient.request(selector, host, port).map(Right.apply)),
      offset = 0
    )

  /** Loads the specified raw text file */
  def loadText(): MainState =
    copy(
      pageContent = Try(port.toInt).flatMap(port => GopherClient.requestText(selector, host, port).map(Right.apply)),
      offset = 0
    )

  /** Loads the specified raw bitmap file */
  def loadBitmap(): MainState =
    copy(
      pageContent = Try(port.toInt).flatMap(port => GopherClient.requestBmp(selector, host, port).map(Left.apply)),
      offset = 0
    )

  /** Loads the nextpage with the search query */
  def performSearch(): MainState =
    copy(query = query + searchInput.map(search => s"\t$search").getOrElse(""), searchInput = None).loadPage()

  /** Text content ignoring errors and binary files */
  val textContent = pageContent.toOption.flatMap(_.toOption).getOrElse(Nil)

  /** Error message, populated if the page loading failed */
  def errorMessage: Option[String] =
    pageContent.failed.toOption
      .map(_.getMessage)

object MainState:
  val defaultHomepage = List(
    GopherClient.GopherItem.info("Welcome to the Scala Gopher Client!"),
    GopherClient.GopherItem.info("Here are some cool links to get you started"),
    GopherClient.GopherItem.info("-------------------------------------------"),
    GopherClient
      .GopherItem('1', "Veronica-2 - The reincarnated Gopher search engine!", "/v2", "gopher.floodgap.com", 70),
    GopherClient.GopherItem('1', "Gopher News - A Google News Reader for Gopher", "/", "gophernews.net", 70),
    GopherClient.GopherItem('1', "HN Gopher - A Hacker News Mirror", "/", "hngopher.com", 70),
    GopherClient.GopherItem('1', "Gopherpedia - The gopher interface to Wikipedia", "/", "gopherpedia.com", 70)
  )
