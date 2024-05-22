package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.rokrok.*
import scala.util.Success
import eu.joaocosta.rokrok.state.MainState
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator

/** Search window */
def searchWindow(colorScheme: ColorScheme): ComponentWithValue[MainState] =
  new ComponentWithValue[MainState]:
    def render(area: Rect, appState: Ref[MainState]): Component[Unit] =
      var triggerSearch = false
      appState.modifyRefs: (query, _, searchInput, _, _, _) =>
        searchInput.get.foreach: searchQuery =>
          val (newSearchInput, _) = window(
            "search_window",
            "Search",
            closable = true,
            skin = WindowSkin.default().copy(colorScheme = colorScheme)
          )(area): windowArea =>
            columns(windowArea.shrink(4).copy(h = 16), 4, padding = 5): column ?=>
              text(column(0), colorScheme.text, "Search:", Font.default, alignRight, centerVertically)
              button("search_button", "Search", ButtonSkin.default().copy(colorScheme = colorScheme))(column(3)):
                triggerSearch = true
              textInput(
                "search_input",
                TextInputSkin.default().copy(colorScheme = colorScheme)
              )(
                column(1) ++ column(2),
                searchQuery
              )
          searchInput := newSearchInput
      appState.modifyIf(triggerSearch)(_.performSearch())
