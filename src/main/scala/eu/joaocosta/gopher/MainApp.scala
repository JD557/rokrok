package eu.joaocosta.gopher

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.minart.graphics.Canvas.Settings
import eu.joaocosta.gopher.components.*

object MainApp:
  val uiContext  = new UiContext()
  val fullArea   = Rect(0, 0, 800, 600)
  val headerSize = 32

  val appState = Ref(MainState())

  def application(inputState: InputState) =
    ui(inputState, uiContext):
      onTop(errorWindow(Rect(0, 0, 400, 200).centerAt(fullArea.centerX, fullArea.centerY))(appState))
      dynamicRows(fullArea, padding = 0): nextRow =>
        header(nextRow(headerSize))(appState)
        itemList(nextRow(maxSize))(appState)

  @main def main() =
    MinartBackend.run(Settings(width = fullArea.w, height = fullArea.h, title = "Gopher"))(application)
