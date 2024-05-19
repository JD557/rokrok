package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.gopher.*
import eu.joaocosta.gopher.state.Settings
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
      Vector("Light", "Dark", "Phosphor"),
      "Skin",
      SelectSkin.default().copy(colorScheme = colorScheme)
    )

  new ComponentWithValue[Settings]:
    def render(area: Rect, settings: Ref[Settings]): Component[Unit] =
      settings.modifyRefs: (fileMenu, skinMenu, newColorScheme, fullScreen) =>
        rectangle(area, colorScheme.secondary)
        dynamicColumns(area, 0):
          fileSelect(fullScreen.get)(fileMenu) match {
            case PanelState(_, 0) =>
              fullScreen.modify(!_)
              fileMenu.modify(_.copy(value = -1))
            case PanelState(_, 1)     => System.exit(0)
            case PanelState(false, _) => fileMenu.modify(_.copy(value = -1))
            case _                    =>
          }

          skinSelect(skinMenu) match {
            case PanelState(_, 0) =>
              newColorScheme := ColorScheme.lightScheme
              skinMenu.modify(_.copy(value = -1))
            case PanelState(_, 1) =>
              newColorScheme := ColorScheme.darkScheme
              skinMenu.modify(_.copy(value = -1))
            case PanelState(_, 2) =>
              newColorScheme := PhosphorTheme
              skinMenu.modify(_.copy(value = -1))
            case PanelState(false, _) =>
              skinMenu.modify(_.copy(value = -1))
            case _ =>
          }
