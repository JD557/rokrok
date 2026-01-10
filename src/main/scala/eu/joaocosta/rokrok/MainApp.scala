package eu.joaocosta.rokrok

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.minart.graphics.Canvas.Settings
import eu.joaocosta.rokrok.components.*
import eu.joaocosta.rokrok.state.*

object MainApp:
  val uiContext = new UiContext()
  val fullArea  = Rect(0, 0, 960, 540)

  val appState = Ref(MainState())

  def application(inputState: InputState) =
    ui(inputState, uiContext):
      appState.modifyRefs: (page, settings) =>
        val colorScheme = settings.get.colorScheme
        val font        = settings.get.font

        onTop:
          errorWindow(colorScheme)(Rect(0, 0, 400, 200).centerAt(fullArea.centerX, fullArea.centerY), page)
          searchWindow(colorScheme)(Rect(0, 0, 400, 50).centerAt(fullArea.centerX, fullArea.centerY), page)
          settingsWindow(colorScheme)(settings)

        dynamicRows(fullArea, padding = 0): nextRow ?=>
          menuBar(colorScheme)(nextRow(20), settings)
          header(colorScheme)(page)
          val contentArea = nextRow.fill()
          rectangle(contentArea, colorScheme.background)
          document(font, colorScheme)(contentArea, page)
          page.get.imageContent.foreach: surface =>
            image(colorScheme)(contentArea, surface)

  @main def main() =
    MinartBackend.run(
      Settings(
        width = fullArea.w,
        height = fullArea.h,
        title = "RokRok",
        fullScreen = false
      ),
      getSettings = appState.get.settings
    )(
      application
    )
