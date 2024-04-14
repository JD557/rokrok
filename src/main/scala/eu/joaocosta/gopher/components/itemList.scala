package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.gopher.*

/** List with all items */
def itemList(area: Rect, colorScheme: ColorScheme): ComponentWithValue[MainState] =
  new ComponentWithValue[MainState]:
    val rowSize    = 16
    val rowPadding = 0
    val maxItems   = area.h / (rowSize + rowPadding)
    val sliderSize = 16

    def render(appState: Ref[MainState]): Component[Unit] =
      dynamicColumns(area, 3): nextColumn =>
        val maxOffset = math.max(0, appState.get.textContent.size - maxItems)
        appState.modifyRefs: (_, _, _, offset, _, _, _) =>
          slider(
            "itemList" |> "scroll",
            nextColumn(-sliderSize),
            0,
            maxOffset,
            SliderSkin.default().copy(colorScheme = colorScheme)
          )(offset)
        val start = appState.get.offset
        val end   = start + maxItems
        rows(nextColumn(maxSize), maxItems, rowPadding): row =>
          appState.get.textContent.zipWithIndex
            .slice(start, end)
            .zip(row)
            .foreach:
              case ((item, idx), itemArea) =>
                gopherItem("itemList" |> idx, itemArea, item, colorScheme)(appState)
