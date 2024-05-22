package eu.joaocosta.rokrok.state

import scala.util.*
import eu.joaocosta.minart.graphics.RamSurface
import eu.joaocosta.interim.PanelState
import scala.concurrent.*
import scala.concurrent.ExecutionContext.Implicits.global
import eu.joaocosta.interim.skins.ColorScheme
import eu.joaocosta.interim.Font
import eu.joaocosta.rokrok.colorschemes.*

/** Application Settings and state for relevant setting components
  */
final case class Settings(
    fileMenu: PanelState[Int] = PanelState.closed(-1),
    skinMenu: PanelState[Int] = PanelState.closed(-1),
    fontMenu: PanelState[Int] = PanelState.closed(-1),
    colorScheme: ColorScheme = Settings.colorSchemes.head._2,
    font: Font = Settings.fonts.head._2,
    fullScreen: Boolean = false
):
  val postProcess: Boolean = colorScheme == PhosphorTheme

object Settings:
  val colorSchemes: Vector[(String, ColorScheme)] =
    Vector(
      "Light"    -> LightTheme,
      "Dark"     -> DarkTheme,
      "Phosphor" -> PhosphorTheme
    )

  val fonts: Vector[(String, Font)] =
    Vector(
      "Unscii-8"  -> Font("unscii", 8, 8),
      "Unscii-16" -> Font("unscii", 16, 8),
      "Bizcat"    -> Font("bizcat", 16, 8)
    )
