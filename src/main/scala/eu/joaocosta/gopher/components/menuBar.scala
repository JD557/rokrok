package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.gopher.*
import eu.joaocosta.gopher.state.Settings

/** Menu bar */
def menuBar(area: Rect, colorScheme: ColorScheme): ComponentWithValue[Settings] =
  new ComponentWithValue[Settings]:
    def render(settings: Ref[Settings]): Component[Unit] =
      settings.modifyRefs: (fileMenu, skinMenu, newColorScheme, fullScreen) =>
        rectangle(area, colorScheme.secondary)
        dynamicColumns(area, 0): nextColumn =>
          select(
            "file",
            nextColumn(128),
            Vector("File", if (fullScreen.get) "Windowed" else "Fullscreen", "Quit"),
            true,
            SelectSkin.default().copy(colorScheme = colorScheme)
          )(fileMenu) match {
            case PanelState(_, 1) =>
              fullScreen.modify(!_)
              fileMenu.modify(_.copy(value = 0))
            case PanelState(_, 2)     => System.exit(0)
            case PanelState(false, _) => fileMenu.modify(_.copy(value = 0))
            case _                    =>
          }

          select(
            "skin",
            nextColumn(128),
            Vector("Skin", "Light", "Dark", "Phosphor"),
            true,
            SelectSkin.default().copy(colorScheme = colorScheme)
          )(skinMenu) match {
            case PanelState(_, 1) =>
              newColorScheme := ColorScheme.lightScheme
              skinMenu.modify(_.copy(value = 0))
            case PanelState(_, 2) =>
              newColorScheme := ColorScheme.darkScheme
              skinMenu.modify(_.copy(value = 0))
            case PanelState(_, 3) =>
              newColorScheme := PhosphorTheme
              skinMenu.modify(_.copy(value = 0))
            case PanelState(false, _) =>
              skinMenu.modify(_.copy(value = 0))
            case _ =>
          }
