package eu.joaocosta.gopher

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.ColorScheme
import eu.joaocosta.minart.graphics.Canvas.Settings
import eu.joaocosta.minart.graphics.{Color => MinartColor}
import eu.joaocosta.gopher.components.*
import eu.joaocosta.gopher.state.*

object MainApp:
  val uiContext = new UiContext()
  val fullArea  = Rect(0, 0, 960, 540)

  val appState = Ref(MainState())

  def application(inputState: InputState) =
    val colorScheme = appState.get.settings.colorScheme
    ui(inputState, uiContext):
      onTop(errorWindow(colorScheme)(Rect(0, 0, 400, 200).centerAt(fullArea.centerX, fullArea.centerY), appState))
      onTop(searchWindow(colorScheme)(Rect(0, 0, 400, 50).centerAt(fullArea.centerX, fullArea.centerY), appState))

      dynamicRows(fullArea, padding = 0): nextRow ?=>
        appState.modifyRefs: (_, _, _, _, _, settings) =>
          menuBar(colorScheme)(nextRow(16), settings)
        header(colorScheme)(appState)
        val contentArea = nextRow.fill()
        rectangle(contentArea, colorScheme.background)
        itemList(colorScheme)(contentArea, appState)
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
