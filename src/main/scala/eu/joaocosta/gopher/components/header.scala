package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.gopher.*

/** Header component with the search bar */
def header(area: Rect, colorScheme: ColorScheme): ComponentWithValue[MainState] =
  new ComponentWithValue[MainState]:
    def render(appState: Ref[MainState]): Component[Unit] =
      var triggerRequest = false
      var goHome         = false
      appState.modifyRefs: (query, _, _, offset, fileMenu, skinMenu, newColorScheme) =>
        dynamicRows(area, 0): nextRow =>
          val menu        = nextRow(16)
          rectangle(menu, colorScheme.secondary)
          dynamicColumns(menu, 0): nextColumn =>
            select(
              "file",
              nextColumn(64),
              Vector("File", "Quit"),
              true,
              SelectSkin.default().copy(colorScheme = colorScheme)
            )(fileMenu) match {
              case PanelState(_, 1)     => System.exit(0)
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
              case PanelState(_, 1)     =>
                newColorScheme := ColorScheme.lightScheme
                skinMenu.modify(_.copy(value = 0))
              case PanelState(_, 2)     =>
                newColorScheme := ColorScheme.darkScheme
                skinMenu.modify(_.copy(value = 0))
              case PanelState(_, 3)     =>
                newColorScheme := PhosphorTheme
                skinMenu.modify(_.copy(value = 0))
              case PanelState(false, _) =>
                skinMenu.modify(_.copy(value = 0))
              case _                    =>
            }

          val quickAccess = nextRow(32)
          rectangle(quickAccess, colorScheme.secondaryHighlight)
          columns(quickAccess.shrink(8), 8, 5): column =>
            text(column(0), colorScheme.text, "Gopher", Font("unscii", 16, 8), alignLeft, centerVertically)
            text(column(1), colorScheme.text, "URL:", Font.default, alignRight, centerVertically)
            textInput("url", column(2) ++ column(5), TextInputSkin.default().copy(colorScheme = colorScheme))(query)
            button("go", column(6), "Go", ButtonSkin.default().copy(colorScheme = colorScheme)):
              triggerRequest = true
            button("home", column(7), "Home", ButtonSkin.default().copy(colorScheme = colorScheme)):
              goHome = true
      appState
        .modifyIf(triggerRequest)(_.load())
        .modifyIf(goHome)(_.loadHome())
