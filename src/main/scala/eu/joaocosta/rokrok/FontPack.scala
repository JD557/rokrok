package eu.joaocosta.rokrok

import eu.joaocosta.interim.*

final case class FontPack(heading: Font, text: Font)

object FontPack {
  val unscii = FontPack(heading = Font("unscii", 16, 8), text = Font("unscii", 8, 8))
  val bizcat = FontPack(heading = Font("bizcat", 32, 16), text = Font("bizcat", 16, 8))
}
