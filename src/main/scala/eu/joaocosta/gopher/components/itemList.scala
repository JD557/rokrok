package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.gopher.*

/** List with all items */
def itemList(area: Rect): ComponentWithValue[MainState] =
  new ComponentWithValue[MainState]:
    val rowSize    = 16
    val rowPadding = 0
    val maxItems   = area.h / (rowSize + rowPadding)
    val sliderSize = 16

    def render(appState: Ref[MainState]): Component[Unit] =
      dynamicColumns(area, 3): nextColumn =>
        val maxOffset = math.max(0, appState.get.content.size - maxItems)
        appState.modifyRefs: (_, _, offset) =>
          slider("itemScroll", nextColumn(-sliderSize), 0, maxOffset)(offset)
        val start = appState.get.offset
        val end   = start + maxItems
        rows(nextColumn(maxSize), maxItems, rowPadding): row =>
          appState.get.content.zipWithIndex
            .slice(start, end)
            .zip(row)
            .foreach:
              case ((item, idx), itemArea) =>
                gopherItem(itemArea, item)(appState)
