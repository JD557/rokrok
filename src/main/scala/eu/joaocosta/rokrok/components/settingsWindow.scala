package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.Settings

/** Settings window */
def settingsWindow(colorScheme: ColorScheme)(settings: Ref[Settings]): Component[Settings] =
  val selectSkin = SelectSkin.default().copy(colorScheme = colorScheme)

  val skinSelect =
    select(
      "skin",
      Settings.colorSchemes.map(_._1),
      "Skin",
      selectSkin
    )

  val fontSelect =
    select(
      "font",
      Settings.fonts.map(_._1),
      "Font",
      selectSkin
    )

  val keyboardLayoutSelect =
    select(
      "keyboardLayout",
      Settings.keyboardLayouts.map(_._1),
      "Keyboard Layout",
      selectSkin
    )

  val aspectRatioSelect =
    select(
      "aspectRatio",
      Settings.aspectRatios.map(_._1),
      "Aspect Ratio",
      selectSkin
    )

  (settings
    .modifyRefs: (panelState, _, skinMenu, fontMenu, keyboardLayoutMenu, aspectRatioMenu, fullScreen) =>
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
            text(summon, colorScheme.text, "Keyboard Layout:")
            keyboardLayoutSelect(keyboardLayoutMenu)
          columns(nextRow(16), 2, 2):
            text(summon, colorScheme.text, "Aspect Ratio:")
            aspectRatioSelect(aspectRatioMenu)
          columns(nextRow(16), 2, 2):
            text(summon, colorScheme.text, "Full Screen:")
            checkbox("settings" |> "fullscreen", skin = CheckboxSkin.default().copy(colorScheme = colorScheme))(
              fullScreen
            )
    )
    .get
