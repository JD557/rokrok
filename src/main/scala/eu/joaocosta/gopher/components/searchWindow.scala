package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.gopher.*
import scala.util.Success

/** Search window */
def searchWindow(area: Rect, colorScheme: ColorScheme): ComponentWithValue[MainState] =
  new ComponentWithValue[MainState]:
    def render(appState: Ref[MainState]): Component[Unit] =
      var triggerSearch = false
      appState.modifyRefs: (query, _, searchInput, _, _) =>
        searchInput.get.foreach: searchQuery =>
          val (newSearchInput, _) = window(
            "search_window",
            area,
            "Search",
            closable = true,
            skin = WindowSkin.default().copy(colorScheme = colorScheme)
          ): windowArea =>
            columns(windowArea.shrink(4).copy(h = 16), 4, padding = 5): column =>
              text(column(0), colorScheme.text, "Search:", Font.default, alignRight, centerVertically)
              button("search_button", column(3), "Search", ButtonSkin.default().copy(colorScheme = colorScheme)):
                triggerSearch = true
              textInput("search_input", column(1) ++ column(2), TextInputSkin.default().copy(colorScheme = colorScheme))(
                searchQuery
              )
          searchInput := newSearchInput
      appState.modifyIf(triggerSearch)(_.performSearch())
