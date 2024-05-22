package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.MainState
import scala.util.*
import scala.concurrent.Future
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator

/** Error window */
def errorWindow(colorScheme: ColorScheme): ComponentWithValue[MainState] =
  new ComponentWithValue[MainState]:
    def render(area: Rect, appState: Ref[MainState]): Component[Unit] =
      appState.get.errorMessage.foreach: message =>
        val (nextState, _) =
          window(
            "error",
            "Error",
            closable = true,
            skin = WindowSkin.default().copy(colorScheme = colorScheme)
          )(area): windowArea =>
            text(windowArea.shrink(4), colorScheme.text, message)
        appState.modifyIf(nextState.isEmpty)(_.copy(pageContent = Future.successful(Right(Nil))))
