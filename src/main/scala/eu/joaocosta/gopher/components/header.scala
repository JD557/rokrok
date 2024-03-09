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
      appState.modifyRefs: (query, _, _, offset, fileMenu) =>
        dynamicRows(area, 0): nextRow =>
          val menu        = nextRow(16)
          val quickAccess = nextRow(32)
          rectangle(menu, colorScheme.secondary)
          select(
            "file",
            menu.copy(w = 64),
            Vector("File", "Quit"),
            true,
            SelectSkin.default().copy(colorScheme = colorScheme)
          )(fileMenu) match {
            case PanelState(_, 1)     => System.exit(0)
            case PanelState(false, _) => fileMenu.modify(_.copy(value = 0))
            case _                    =>
          }
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
