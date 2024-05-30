package eu.joaocosta.rokrok.state

import scala.concurrent.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.*

import eu.joaocosta.interim.PanelState
import eu.joaocosta.interim.skins.ColorScheme
import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.rokrok.GopherClient

/** Main application state
  */
final case class MainState(
    page: Page = Page(),
    settings: Settings = Settings()
)
