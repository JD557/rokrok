package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.gopher.*
import scala.util.*
import scala.concurrent.Future

/** Error window */
def errorWindow(area: Rect, colorScheme: ColorScheme): ComponentWithValue[MainState] =
  new ComponentWithValue[MainState]:
    def render(appState: Ref[MainState]): Component[Unit] =
      appState.get.errorMessage.foreach: message =>
        val (nextState, _) =
          window(
            "error",
            area,
            "Error",
            closable = true,
            skin = WindowSkin.default().copy(colorScheme = colorScheme)
          ): windowArea =>
            text(windowArea.shrink(4), colorScheme.text, message)
        appState.modifyIf(nextState.isEmpty)(_.copy(pageContent = Future.successful(Right(Nil))))
