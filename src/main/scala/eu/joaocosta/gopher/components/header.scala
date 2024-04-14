package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.gopher.*
import eu.joaocosta.gopher.state.MainState

/** Header component with the search bar */
def header(area: Rect, colorScheme: ColorScheme): ComponentWithValue[MainState] =
  new ComponentWithValue[MainState]:
    def render(appState: Ref[MainState]): Component[Unit] =
      var triggerRequest = false
      var goHome         = false

      appState.modifyRefs: (query, _, _, offset, _) =>
        rectangle(area, colorScheme.secondaryHighlight)
        columns(area.shrink(8), 8, 5): column =>
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
