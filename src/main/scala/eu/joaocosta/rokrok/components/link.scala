package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.ColorScheme
import eu.joaocosta.rokrok.*

final def link(
    id: ItemId,
    area: Rect,
    label: String,
    font: Font,
    colorScheme: ColorScheme
): Component[Boolean] =
  val itemStatus = UiContext.registerItem(id, area)
  val color      = itemStatus match {
    case UiContext.ItemStatus(true, _, _, _) =>
      colorScheme.primaryHighlight
    case UiContext.ItemStatus(_, true, _, _) =>
      colorScheme.primaryHighlight
    case _ =>
      colorScheme.primary
  }
  text(area, color, label, font)
  itemStatus.clicked
