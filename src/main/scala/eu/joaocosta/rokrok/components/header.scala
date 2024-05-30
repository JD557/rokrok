package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.MainState
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator

/** Header component with the search bar */
def header(colorScheme: ColorScheme): DynamicComponentWithValue[MainState] =
  val headerSize = 32

  new DynamicComponentWithValue[MainState]:
    def allocateArea(using allocator: AreaAllocator): Rect =
      allocator.allocate(maxSize, headerSize)

    def render(area: Rect, appState: Ref[MainState]): Component[Unit] =
      rectangle(area, colorScheme.secondaryHighlight)
      columns(area.shrink(8), 10, 5): column ?=>
        text(column(0), colorScheme.text, "RokRok", Font("unscii", 16, 8), alignLeft, centerVertically)
        text(column(1), colorScheme.text, "URL:", Font.default, alignRight, centerVertically)
        appState.modifyRefs: (query, _, _, _, _, _) =>
          textInput("url", TextInputSkin.default().copy(colorScheme = colorScheme))(column(2) ++ column(6), query)

        button("go", "Go", ButtonSkin.default().copy(colorScheme = colorScheme))(column(7)):
          appState.modify(_.load())
        if (appState.get.history.nonEmpty)
          button("back", "Back", ButtonSkin.default().copy(colorScheme = colorScheme))(column(8)):
            appState.modify(_.goBack())
        button("home", "Home", ButtonSkin.default().copy(colorScheme = colorScheme))(column(9)):
          appState.modify(_.loadHome())
