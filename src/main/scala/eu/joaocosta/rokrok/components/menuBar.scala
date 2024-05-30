package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.Settings
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator

/** Menu bar */
def menuBar(colorScheme: ColorScheme): ComponentWithValue[Settings] =
  val fileSelect =
    select(
      "file",
      Vector("Settings", "Quit"),
      "File",
      SelectSkin.default().copy(colorScheme = colorScheme)
    )

  new ComponentWithValue[Settings]:
    def render(area: Rect, settings: Ref[Settings]): Component[Unit] =
      settings.modifyRefs: (settingsPannel, fileMenu, _, _, _) =>
        rectangle(area, colorScheme.secondary)
        dynamicColumns(area, 0):
          fileSelect(fileMenu) match
            case PanelState(_, 0) =>
              if (settingsPannel.get.isClosed)
                settingsPannel := PanelState.open(
                  Rect(100, 100, 400, 200).centerAt(MainApp.fullArea.centerX, MainApp.fullArea.centerY)
                )
              fileMenu.modify(_.copy(value = -1))
            case PanelState(_, 1)     => System.exit(0)
            case PanelState(false, _) => fileMenu.modify(_.copy(value = -1))
            case _                    => ()
