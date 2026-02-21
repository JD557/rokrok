package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator
import eu.joaocosta.interim.skins.ColorScheme
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.Page

/** Gopher item list */
def docElement(
    id: ItemId,
    item: Document.Element,
    font: FontPack,
    colorScheme: ColorScheme
): DynamicComponentWithValue[Page] =
  new DynamicComponentWithValue[Page]:

    val itemDescription = item match
      case _: Document.Element.Text            => ""
      case _: Document.Element.MonospaceText   => ""
      case _: Document.Element.Heading         => ""
      case _: Document.Element.Error           => "[ERROR] "
      case _: Document.Element.Link            => ""
      case _: Document.Element.Input           => "[INPUT] "
      case _: Document.Element.Image           => "[IMAGE] "
      case _: Document.Element.File            => "[FILE] "
      case Document.Element.Other(_, datatype) => s"[$datatype] "

    val innerText =
      item match
        case Document.Element.Text(content) =>
          itemDescription + content + " "
        case Document.Element.MonospaceText(content) =>
          itemDescription + content + " "
        case Document.Element.Heading(content) =>
          itemDescription + content + " "
        case Document.Element.Error(description) =>
          itemDescription + description + " "
        case Document.Element.Link(description, _) =>
          itemDescription + description + " "
        case Document.Element.Input(description, _) =>
          itemDescription + description + " "
        case Document.Element.Image(description, _) =>
          itemDescription + description + " "
        case Document.Element.File(description, _, _) =>
          itemDescription + description + " "
        case Document.Element.Other(description, _) =>
          itemDescription + description + " "

    def allocateArea(using allocator: AreaAllocator): Rect =
      item match
        case _: Document.Element.Heading =>
          allocator.allocate(innerText, font.heading, paddingH = 1)
        case _ =>
          allocator.allocate(innerText, font.text, paddingH = 1)

    def render(area: Rect, pageState: Ref[Page]): Component[Unit] =
      item match
        case _: Document.Element.Text =>
          text(area, colorScheme.text, innerText, font.text)
        case Document.Element.MonospaceText(content) =>
          if (!area.isEmpty)
            rectangle(area.grow(2), colorScheme.secondary)
            // TODO: Change font
            text(area, colorScheme.text, innerText, font.text)
        case Document.Element.Heading(content) =>
          text(area, colorScheme.text, innerText, font.heading)
        case Document.Element.Error(description) =>
          // TODO: Change color
          text(area, colorScheme.text, innerText, font.heading)
        case Document.Element.Link(description, url) =>
          pageState.modifyIf(link(id |> description, area, innerText, font.text, colorScheme))(
            _.setUrl(url).load()
          )
        case Document.Element.Input(description, url) =>
          pageState.modifyIf(link(id |> description, area, innerText, font.text, colorScheme))(
            _.setUrl(url).copy(searchInput = Some(""))
          )
        case Document.Element.Image(description, url) =>
          if (url.endsWith(".bmp"))
            pageState.modifyIf(link(id |> description, area, innerText, font.text, colorScheme))(
              _.setUrl(url).loadBitmap()
            )
          else
            text(area, colorScheme.text, innerText, font.text)
        case Document.Element.File(description, url, datatype) =>
          if (datatype == "TEXT")
            pageState.modifyIf(link(id |> description, area, innerText, font.text, colorScheme))(
              _.setUrl(url).load()
            )
          else
            text(area, colorScheme.text, innerText, font.text)
        case Document.Element.Other(description, _) =>
          text(area, colorScheme.text, innerText, font.text)
