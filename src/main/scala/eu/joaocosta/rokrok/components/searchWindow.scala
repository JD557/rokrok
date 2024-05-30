package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.Page

/** Search window */
def searchWindow(colorScheme: ColorScheme): ComponentWithValue[Page] =
  new ComponentWithValue[Page]:
    def render(area: Rect, pageState: Ref[Page]): Component[Unit] =
      var triggerSearch = false
      pageState.get.searchInput.foreach: searchQuery =>
        val (newSearchInput, _) = window(
          "search_window",
          "Search",
          closable = true,
          skin = WindowSkin.default().copy(colorScheme = colorScheme),
          handleSkin = HandleSkin.default().copy(colorScheme = colorScheme)
        )(area): windowArea =>
          columns(windowArea.shrink(4).copy(h = 16), 4, padding = 5): column ?=>
            text(column(0), colorScheme.text, "Search:", Font.default, alignRight, centerVertically)
            val newSearch = textInput("search_input", TextInputSkin.default().copy(colorScheme = colorScheme))(
              column(1) ++ column(2),
              searchQuery
            )
            pageState.modify(_.copy(searchInput = Some(newSearch)))
            button("search_button", "Search", ButtonSkin.default().copy(colorScheme = colorScheme))(column(3)):
              pageState.modify(_.performSearch())
