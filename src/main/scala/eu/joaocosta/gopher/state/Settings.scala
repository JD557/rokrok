package eu.joaocosta.gopher.state

import scala.util.*
import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.interim.PanelState
import scala.concurrent.*
import scala.concurrent.ExecutionContext.Implicits.global
import eu.joaocosta.interim.skins.ColorScheme
import eu.joaocosta.gopher.components.PhosphorTheme

/** Application Settings and state for relevant setting components
  */
final case class Settings(
    fileMenu: PanelState[Int] = PanelState.closed(-1),
    skinMenu: PanelState[Int] = PanelState.closed(-1),
    colorScheme: ColorScheme = ColorScheme.lightScheme,
    fullScreen: Boolean = false
) {
  val postProcess: Boolean = colorScheme == PhosphorTheme
}
