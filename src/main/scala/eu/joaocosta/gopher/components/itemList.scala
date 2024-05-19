package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.gopher.*
import eu.joaocosta.gopher.state.MainState
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator

/** List with all items */
def itemList(colorScheme: ColorScheme): DynamicComponentWithValue[MainState] =
  new DynamicComponentWithValue[MainState]:
    val rowSize    = 16
    val rowPadding = 0

    def allocateArea(using allocator: AreaAllocator): Rect = allocator.fill()

    def render(area: Rect, appState: Ref[MainState]): Component[Unit] =
      val maxItems = area.h / (rowSize + rowPadding)
      dynamicColumns(area, 3, alignRight): nextColumn ?=>
        val maxOffset = math.max(0, appState.get.textContent.size - maxItems)
        appState.modifyRefs: (_, _, _, _, offset, _) =>
          slider(
            "itemList" |> "scroll",
            0,
            maxOffset,
            SliderSkin.default().copy(colorScheme = colorScheme)
          )(offset)
        val start = appState.get.offset
        val end   = start + maxItems
        rows(nextColumn(maxSize), maxItems, rowPadding): row ?=>
          appState.get.textContent.zipWithIndex
            .slice(start, end)
            .zip(row)
            .foreach:
              case ((item, idx), itemArea) =>
                gopherItem("itemList" |> idx, item, colorScheme)(itemArea, appState)
