package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator
import eu.joaocosta.interim.skins.*
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.Page

/** Document component */
def document(font: FontPack, colorScheme: ColorScheme): DynamicComponentWithValue[Page] =
  new DynamicComponentWithValue[Page]:
    val rowPadding = 0
    val rowSize    = font.heading.lineHeight + 2 * rowPadding

    def allocateArea(using allocator: AreaAllocator): Rect = allocator.fill()

    def render(area: Rect, pageState: Ref[Page]): Component[Unit] =
      val maxItems = area.h / (rowSize + rowPadding)
      dynamicColumns(area, 3, alignRight): nextColumn ?=>
        val maxOffset = math.max(0, pageState.get.textContent.size - maxItems)
        pageState.modifyRefs: (_, _, _, _, offset) =>
          slider(
            "document" |> "scroll",
            0,
            maxOffset,
            SliderSkin.default().copy(colorScheme = colorScheme)
          )(nextColumn(16), offset)
        val start = pageState.get.offset
        dynamicRows(nextColumn(maxSize).shrink(4), rowPadding): nextRow ?=>
          pageState.get.textContent.zipWithIndex
            .drop(start)
            .foreach:
              case (item, idx) =>
                docElement("document" |> idx, item, font, colorScheme)(pageState)
