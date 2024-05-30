package eu.joaocosta.rokrok.state

import eu.joaocosta.interim.*
import eu.joaocosta.interim.skins.ColorScheme
import eu.joaocosta.rokrok.colorschemes.*

/** Application Settings and state for relevant setting components
  */
final case class Settings(
    panelState: PanelState[Rect] = PanelState.closed(Rect(0, 0, 0, 0)),
    fileMenu: PanelState[Int] = PanelState.closed(-1),
    skinMenu: PanelState[Int] = PanelState.closed(0),
    fontMenu: PanelState[Int] = PanelState.closed(0),
    fullScreen: Boolean = false
):
  val colorScheme: ColorScheme = Settings.colorSchemes(skinMenu.value)._2
  val font: Font               = Settings.fonts(fontMenu.value)._2
  val postProcess: Boolean     = colorScheme == PhosphorTheme

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
