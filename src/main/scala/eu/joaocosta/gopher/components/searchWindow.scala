package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.gopher.*
import scala.util.Success

/** Search window */
def searchWindow(area: Rect): ComponentWithValue[MainState] =
  new ComponentWithValue[MainState]:
    def render(appState: Ref[MainState]): Component[Unit] =
      var triggerSearch = false
      appState.modifyRefs: (query, _, searchInput, _) =>
        searchInput.get.foreach: searchQuery =>
          val (newSearchInput, _) = window("search_window", area, "Search", closable = true): windowArea =>
            columns(windowArea.shrink(4).copy(h = 16), 4, padding = 5): column =>
              text(column(0), Color(0, 0, 0), "Search:", Font.default, alignRight, centerVertically)
              triggerSearch = button("search_button", column(3), "Search")
              textInput("search_input", column(1) ++ column(2))(searchQuery)
          searchInput := newSearchInput
      appState.modifyIf(triggerSearch)(_.performSearch())
