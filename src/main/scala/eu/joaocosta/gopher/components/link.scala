package eu.joaocosta.gopher.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.gopher.*

final def link(
    id: ItemId,
    area: Rect,
    label: String
): Component[Boolean] =
  val itemStatus = UiContext.registerItem(id, area)
  val color = itemStatus match {
    case UiContext.ItemStatus(true, false, _, _) =>
      skins.ColorScheme.lightPrimary
    case UiContext.ItemStatus(_, true, _, _) =>
      skins.ColorScheme.lightPrimaryHighlight
    case _ =>
      skins.ColorScheme.lightPrimaryShadow
  }
  text(area, color, label)
  itemStatus.clicked
