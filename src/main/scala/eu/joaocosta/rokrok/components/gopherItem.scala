package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.ColorScheme
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.MainState
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator

/** Gopher item list */
def gopherItem(
    id: ItemId,
    item: GopherClient.GopherItem,
    font: Font,
    colorScheme: ColorScheme
): ComponentWithValue[MainState] =
  new ComponentWithValue[MainState]:
    def render(area: Rect, appState: Ref[MainState]): Component[Unit] =
      lazy val targetUrl = s"${item.hostname}:${item.port}/${item.itemType}${item.selector}"
      columns(area.shrink(3).copy(h = area.h), 5, 2): column ?=>
        item.itemType match
          case '0' | '1' | '+' =>
            appState.modifyIf(link(id |> item.userString, column(0) ++ column(3), item.userString, font, colorScheme))(
              _.copy(query = targetUrl).load()
            )
          case '7' =>
            appState.modifyIf(link(id |> item.userString, column(0) ++ column(3), item.userString, font, colorScheme))(
              _.copy(query = targetUrl, searchInput = Some(""))
            )
          case 'I' | ':' | '9' if item.selector.endsWith(".bmp") =>
            appState.modifyIf(link(id |> item.userString, column(0) ++ column(3), item.userString, font, colorScheme))(
              _.copy(query = targetUrl).loadBitmap()
            )
          case _ =>
            text(column(0) ++ column(3), colorScheme.text, item.userString, font)
        val itemDescription = item.itemType match
          case '0'                         => "[TEXT]"
          case '1' | '+'                   => "[LINK]"
          case '2'                         => "[CCSO]"
          case '3'                         => "[ERROR]"
          case '4'                         => "[BINEX]"
          case '5'                         => "[DOS]"
          case '6'                         => "[UUENC]"
          case '7'                         => "[SEARCH]"
          case '8' | 'T'                   => "[TELNET]"
          case '9'                         => "[BINARY]"
          case 'I' | ':'                   => "[IMAGE]"
          case 'p'                         => "[PNG]"
          case 'g'                         => "[GIF]"
          case ';'                         => "[VIDEO]"
          case '<' | 's'                   => "[SOUND]"
          case 'd' | 'h' | 'r' | 'P' | 'x' => "[DOCUMENT]"
          case _                           => ""

        text(column(4), colorScheme.text, itemDescription, font, alignLeft, centerVertically)
