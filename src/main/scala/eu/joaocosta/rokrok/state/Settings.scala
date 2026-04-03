package eu.joaocosta.rokrok.state

import eu.joaocosta.interim.*
import eu.joaocosta.interim.skins.ColorScheme
import eu.joaocosta.minart.input.KeyboardLayout
import eu.joaocosta.rokrok.FontPack
import eu.joaocosta.rokrok.colorschemes.*

/** Application Settings and state for relevant setting components
  */
final case class Settings(
    panelState: PanelState[Rect] = PanelState.closed(Rect(0, 0, 0, 0)),
    fileMenu: PanelState[Int] = PanelState.closed(-1),
    skinMenu: PanelState[Int] = PanelState.closed(0),
    fontMenu: PanelState[Int] = PanelState.closed(0),
    keyboardLayoutMenu: PanelState[Int] = PanelState.closed(0),
    fullScreen: Boolean = false
):
  val colorScheme: ColorScheme       = Settings.colorSchemes(skinMenu.value)._2
  val font: FontPack                 = Settings.fonts(fontMenu.value)._2
  val postProcess: Boolean           = colorScheme == PhosphorTheme
  val keyboardLayout: KeyboardLayout = Settings.keyboardLayouts(keyboardLayoutMenu.value)._2

object Settings:
  val colorSchemes: Vector[(String, ColorScheme)] =
    Vector(
      "Light"    -> LightTheme,
      "Dark"     -> DarkTheme,
      "Phosphor" -> PhosphorTheme
    )

  val fonts: Vector[(String, FontPack)] =
    Vector(
      "Unscii" -> FontPack.unscii,
      "Bizcat" -> FontPack.bizcat
    )

  val keyboardLayouts: Vector[(String, KeyboardLayout)] =
    Vector(
      "en-US" -> KeyboardLayout.us,
      "en-GB" -> KeyboardLayout.uk,
      "es-ES" -> KeyboardLayout.es,
      "pt-PT" -> KeyboardLayout.pt
    )
