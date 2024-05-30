package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.Page
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator

/** List with all items */
def itemList(font: Font, colorScheme: ColorScheme): DynamicComponentWithValue[Page] =
  new DynamicComponentWithValue[Page]:
    val rowPadding = 0
    val rowSize    = font.lineHeight + 2 * rowPadding

    def allocateArea(using allocator: AreaAllocator): Rect = allocator.fill()

    def render(area: Rect, pageState: Ref[Page]): Component[Unit] =
      val maxItems = area.h / (rowSize + rowPadding)
      dynamicColumns(area, 3, alignRight): nextColumn ?=>
        val maxOffset = math.max(0, pageState.get.textContent.size - maxItems)
        pageState.modifyRefs: (_, _, _, _, offset) =>
          slider(
            "itemList" |> "scroll",
            0,
            maxOffset,
            SliderSkin.default().copy(colorScheme = colorScheme)
          )(nextColumn(16), offset)
        val start = pageState.get.offset
        val end   = start + maxItems
        rows(nextColumn(maxSize), maxItems, rowPadding): row ?=>
          pageState.get.textContent.zipWithIndex
            .slice(start, end)
            .zip(row)
            .foreach:
              case ((item, idx), itemArea) =>
                gopherItem("itemList" |> idx, item, font, colorScheme)(itemArea, pageState)
