package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.Settings
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator

/** Menu bar */
def menuBar(colorScheme: ColorScheme): ComponentWithValue[Settings] =
  def fileSelect(fullScreen: Boolean) =
    select(
      "file",
      Vector(if (fullScreen) "Windowed" else "Fullscreen", "Quit"),
      "File",
      SelectSkin.default().copy(colorScheme = colorScheme)
    )

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

  new ComponentWithValue[Settings]:
    def render(area: Rect, settings: Ref[Settings]): Component[Unit] =
      settings.modifyRefs: (fileMenu, skinMenu, fontMenu, newColorScheme, font, fullScreen) =>
        rectangle(area, colorScheme.secondary)
        dynamicColumns(area, 0):
          fileSelect(fullScreen.get)(fileMenu) match
            case PanelState(_, 0) =>
              fullScreen.modify(!_)
              fileMenu.modify(_.copy(value = -1))
            case PanelState(_, 1)     => System.exit(0)
            case PanelState(false, _) => fileMenu.modify(_.copy(value = -1))
            case _                    => ()

          skinSelect(skinMenu) match
            case PanelState(_, idx) if Settings.colorSchemes.indices.contains(idx) =>
              newColorScheme := Settings.colorSchemes(idx)._2
              skinMenu.modify(_.copy(value = -1))
            case PanelState(false, _) =>
              skinMenu.modify(_.copy(value = -1))
            case _ => ()

          fontSelect(fontMenu) match
            case PanelState(_, idx) if Settings.fonts.indices.contains(idx) =>
              font := Settings.fonts(idx)._2
              fontMenu.modify(_.copy(value = -1))
            case PanelState(false, _) =>
              fontMenu.modify(_.copy(value = -1))
            case _ => ()
