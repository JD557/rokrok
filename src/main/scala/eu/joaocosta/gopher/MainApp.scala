package eu.joaocosta.gopher

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.minart.graphics.Canvas.Settings
import eu.joaocosta.minart.graphics.{Color => MinartColor}
import eu.joaocosta.gopher.components.*

object MainApp:
  val uiContext  = new UiContext()
  val fullArea   = Rect(0, 0, 960, 540)
  val headerSize = 48

  val appState = Ref(MainState())

  val colorScheme = PhosphorTheme

  def application(inputState: InputState) =
    ui(inputState, uiContext):
      onTop(errorWindow(Rect(0, 0, 400, 200).centerAt(fullArea.centerX, fullArea.centerY), colorScheme)(appState))
      onTop(searchWindow(Rect(0, 0, 400, 50).centerAt(fullArea.centerX, fullArea.centerY), colorScheme)(appState))
      dynamicRows(fullArea, padding = 0): nextRow =>
        header(nextRow(headerSize), colorScheme)(appState)
        val contentArea = nextRow(maxSize)
        itemList(contentArea, colorScheme)(appState)
        appState.get.pageContent.toOption
          .flatMap(_.left.toOption)
          .foreach: image =>
            custom(contentArea, colorScheme.background, image)

  @main def main() =
    MinartBackend.run(
      Settings(
        width = fullArea.w,
        height = fullArea.h,
        clearColor = MinartColor(colorScheme.background.r, colorScheme.background.g, colorScheme.background.b),
        title = "Gopher",
        fullScreen = true
      )
    )(
      application
    )
