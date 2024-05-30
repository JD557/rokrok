package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.Page
import scala.util.*
import scala.concurrent.Future
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator

/** Error window */
def errorWindow(colorScheme: ColorScheme): ComponentWithValue[Page] =
  new ComponentWithValue[Page]:
    def render(area: Rect, pageState: Ref[Page]): Component[Unit] =
      pageState.get.errorMessage.foreach: message =>
        val (nextState, _) =
          window(
            "error",
            "Error",
            closable = true,
            skin = WindowSkin.default().copy(colorScheme = colorScheme),
            handleSkin = HandleSkin.default().copy(colorScheme = colorScheme)
          )(area): windowArea =>
            text(windowArea.shrink(4), colorScheme.text, message)
        pageState.modifyIf(nextState.isEmpty)(_.copy(content = Future.successful(Right(Nil))))
