package eu.joaocosta.rokrok.components

import eu.joaocosta.interim.*
import eu.joaocosta.interim.InterIm.*
import eu.joaocosta.interim.skins.*
import eu.joaocosta.rokrok.*
import eu.joaocosta.rokrok.state.Settings
import scala.util.Success
import eu.joaocosta.rokrok.state.MainState
import eu.joaocosta.interim.LayoutAllocator.AreaAllocator
import eu.joaocosta.minart.graphics.RamSurface

/** Custom component to draw an image */
def image(colorScheme: ColorScheme): ComponentWithValue[RamSurface] =
  new ComponentWithValue[RamSurface]:
    def render(area: Rect, suface: Ref[RamSurface]): Component[Unit] =
      custom(area, colorScheme.background, suface)
