package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.gopher.*
import eu.joaocosta.gopher.state.MainState
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator

/** Header component with the search bar */
def header(colorScheme: ColorScheme): DynamicComponentWithValue[MainState] =
  val headerSize = 32

  new DynamicComponentWithValue[MainState]:
    def allocateArea(using allocator: AreaAllocator): Rect =
      allocator.allocate(maxSize, headerSize)

    def render(area: Rect, appState: Ref[MainState]): Component[Unit] =
      var triggerRequest = false
      var goHome         = false

      appState.modifyRefs: (query, _, _, offset, _) =>
        rectangle(area, colorScheme.secondaryHighlight)
        columns(area.shrink(8), 8, 5): column ?=>
          text(column(0), colorScheme.text, "Gopher", Font("unscii", 16, 8), alignLeft, centerVertically)
          text(column(1), colorScheme.text, "URL:", Font.default, alignRight, centerVertically)
          textInput("url", TextInputSkin.default().copy(colorScheme = colorScheme))(column(2) ++ column(5), query)
          button("go", "Go", ButtonSkin.default().copy(colorScheme = colorScheme))(column(6)):
            triggerRequest = true
          button("home", "Home", ButtonSkin.default().copy(colorScheme = colorScheme))(column(7)):
            goHome = true
      appState
        .modifyIf(triggerRequest)(_.load())
        .modifyIf(goHome)(_.loadHome())
