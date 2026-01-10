package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.ColorScheme
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.Page

/** Gopher item list */
def docElement(
    id: ItemId,
    item: Document.Element,
    font: Font,
    colorScheme: ColorScheme
): ComponentWithValue[Page] =
  new ComponentWithValue[Page]:
    def render(area: Rect, pageState: Ref[Page]): Component[Unit] =
      columns(area.shrink(3).copy(h = area.h), 5, 2): column ?=>
        item match
          case Document.Element.Text(content) =>
            text(column(0) ++ column(3), colorScheme.text, content, font)
          case Document.Element.Error(description) =>
            // TODO: Change color
            text(column(0) ++ column(3), colorScheme.text, description, font)
          case Document.Element.Link(description, url) =>
            pageState.modifyIf(link(id |> description, column(0) ++ column(3), description, font, colorScheme))(
              _.copy(query = url).load()
            )
          case Document.Element.Input(description, url) =>
            pageState.modifyIf(link(id |> description, column(0) ++ column(3), description, font, colorScheme))(
              _.copy(query = url, searchInput = Some(""))
            )
          case Document.Element.Image(description, url) =>
            if (url.endsWith(".bmp"))
              pageState.modifyIf(link(id |> description, column(0) ++ column(3), description, font, colorScheme))(
                _.copy(query = url).loadBitmap()
              )
            else
              text(column(0) ++ column(3), colorScheme.text, description, font)
          case Document.Element.File(description, url, datatype) =>
            if (datatype == "TEXT")
              pageState.modifyIf(link(id |> description, column(0) ++ column(3), description, font, colorScheme))(
                _.copy(query = url).load()
              )
            else
              text(column(0) ++ column(3), colorScheme.text, description, font)
          case Document.Element.Other(description, _) =>
            text(column(0) ++ column(3), colorScheme.text, description, font)
        val itemDescription = item match
          case _: Document.Element.Text            => ""
          case _: Document.Element.Error           => "[ERROR]"
          case _: Document.Element.Link            => "[LINK]"
          case _: Document.Element.Input           => "[INPUT]"
          case _: Document.Element.Image           => "[IMAGE]"
          case _: Document.Element.File            => "[FILE]"
          case Document.Element.Other(_, datatype) => s"[$datatype]"

        text(column(4), colorScheme.text, itemDescription, font, alignLeft, centerVertically)
