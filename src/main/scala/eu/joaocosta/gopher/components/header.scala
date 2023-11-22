package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.gopher.*

/** Header component with the search bar */
def header(area: Rect): ComponentWithValue[MainState] =
  new ComponentWithValue[MainState]:
    val headerColor = Color(200, 200, 220)
    val textColor   = Color(0, 0, 0)

    def render(appState: Ref[MainState]): Component[Unit] =
      var triggerRequest = false
      var goHome         = false
      appState.modifyRefs: (query, _, _, offset) =>
        rectangle(area, headerColor)
        columns(area.shrink(8), 8, 5): column =>
          text(column(0), textColor, "Gopher", Font("unscii", 16, 8), alignLeft, centerVertically)
          text(column(1), textColor, "URL:", Font.default, alignRight, centerVertically)
          textInput("url", column(2) ++ column(5))(query)
          triggerRequest = button("go", column(6), "Go")
          goHome = button("home", column(7), "Home")
      appState
        .modifyIf(triggerRequest)(_.load())
        .modifyIf(goHome)(_.loadHome())
