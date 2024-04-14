package eu.joaocosta.gopher

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.ColorScheme
import eu.joaocosta.minart.graphics.Canvas.Settings
import eu.joaocosta.minart.graphics.{Color => MinartColor}
import eu.joaocosta.gopher.components.*
import eu.joaocosta.gopher.state.*

object MainApp:
  val uiContext  = new UiContext()
  val fullArea   = Rect(0, 0, 960, 540)
  val menuSize   = 16
  val headerSize = 32

  val appState = Ref(MainState())

  def application(inputState: InputState) =
    val colorScheme = appState.get.settings.colorScheme
    ui(inputState, uiContext):
      onTop(errorWindow(Rect(0, 0, 400, 200).centerAt(fullArea.centerX, fullArea.centerY), colorScheme)(appState))
      onTop(searchWindow(Rect(0, 0, 400, 50).centerAt(fullArea.centerX, fullArea.centerY), colorScheme)(appState))

      dynamicRows(fullArea, padding = 0): nextRow =>
        appState.modifyRefs: (_, _, _, _, settings) =>
          menuBar(nextRow(menuSize), colorScheme)(settings)
        header(nextRow(headerSize), colorScheme)(appState)
        val contentArea = nextRow(maxSize)
        rectangle(contentArea, colorScheme.background)
        itemList(contentArea, colorScheme)(appState)
        appState.get.pageContent.value
          .flatMap(_.toOption)
          .flatMap(_.left.toOption)
          .foreach: image =>
            custom(contentArea, colorScheme.background, image)

  @main def main() =
    MinartBackend.run(
      Settings(
        width = fullArea.w,
        height = fullArea.h,
        title = "Gopher",
        fullScreen = false
      ),
      getSettings = appState.get.settings
    )(
      application
    )
