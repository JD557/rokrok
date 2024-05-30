package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.Settings
import scala.util.Success
import eu.joaocosta.rokrok.state.MainState
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator

/** Settings window */
def settingsWindow(colorScheme: ColorScheme)(settings: Ref[Settings]): Component[Settings] =
  val skinSelect =
    select(
      "skin",
      Settings.colorSchemes.map(_._1),
      "Skin",
      SelectSkin.default().copy(colorScheme = colorScheme)
    )

  val fontSelect =
    select(
      "font",
      Settings.fonts.map(_._1),
      "Font",
      SelectSkin.default().copy(colorScheme = colorScheme)
    )

  (settings
    .modifyRefs: (panelState, _, skinMenu, fontMenu, fullScreen) =>
      window(
        "settings",
        "Settings",
        closable = true,
        movable = true,
        resizable = false,
        skin = WindowSkin.default().copy(colorScheme = colorScheme),
        handleSkin = HandleSkin.default().copy(colorScheme = colorScheme)
      )(panelState): windowArea =>
        dynamicRows(windowArea.shrink(4), 2): nextRow ?=>
          columns(nextRow(16), 2, 2):
            text(summon, colorScheme.text, "Color Scheme:")
            skinSelect(skinMenu)
          columns(nextRow(16), 2, 2):
            text(summon, colorScheme.text, "Font:")
            fontSelect(fontMenu)
          columns(nextRow(16), 2, 2):
            text(summon, colorScheme.text, "Full Screen:")
            checkbox("settings" |> "fullscreen", skin = CheckboxSkin.default().copy(colorScheme = colorScheme))(
              fullScreen
            )
    )
    .get
