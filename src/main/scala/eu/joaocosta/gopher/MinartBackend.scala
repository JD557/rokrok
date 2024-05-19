package eu.joaocosta.gopher

import scala.concurrent.Future

import eu.joaocosta.interim.*
import eu.joaocosta.minart.backend.defaults.given
import eu.joaocosta.minart.graphics.image.*
import eu.joaocosta.minart.graphics.{Color => MinartColor, *}
import eu.joaocosta.minart.input.*
import eu.joaocosta.minart.runtime.*

object MinartBackend:

  trait MinartFont:
    def charWidth(char: Char): Int
    def coloredChar(char: Char, color: MinartColor): SurfaceView

  case class BitmapFont(
      file: String,
      width: Int,
      height: Int,
      fontFirstChar: Char = '\u0000'
  ) extends MinartFont:
    private val spriteSheet =
      SpriteSheet(Image.loadBmpImage(Resource(file)).get, width, height)
    def charWidth(char: Char): Int = width
    def coloredChar(char: Char, color: MinartColor): SurfaceView =
      spriteSheet.getSprite(char.toInt - fontFirstChar.toInt).map {
        case MinartColor(255, 255, 255) => color
        case c                          => MinartColor(255, 0, 255)
      }

  case class BitmapFontPack(fonts: List[BitmapFont]):
    val sortedFonts = fonts.sortBy(_.height)
    def withSize(fontSize: Int): MinartFont =
      val baseFont = sortedFonts
        .filter(_.height <= fontSize)
        .lastOption
        .getOrElse(sortedFonts.head)
      if (baseFont.height == fontSize) baseFont
      else
        val scale = fontSize / baseFont.height.toDouble
        new MinartFont:
          def charWidth(char: Char): Int = (baseFont.width * scale).toInt
          def coloredChar(char: Char, color: MinartColor): SurfaceView =
            baseFont.coloredChar(char, color).scale(scale)

  // http://viznut.fi/unscii/
  private val unscii = BitmapFontPack(
    List(
      BitmapFont("assets/unscii-8.bmp", 8, 8, ' '),
      BitmapFont("assets/unscii-16.bmp", 8, 16, ' ')
    )
  )
  // https://robey.lag.net/2020/02/09/bizcat-bitmap-font.html
  private val bizcat = BitmapFontPack(
    List(
      BitmapFont("assets/bizcat.bmp", 8, 16, '\u0000'),
    )
  )

  private def processKeyboard(keyboardInput: KeyboardInput): String =
    import KeyboardInput.Key._
    keyboardInput.events
      .collect { case KeyboardInput.Event.Pressed(key) => key }
      .flatMap {
        case Enter => ""
        case x =>
          x.baseChar
            .map(char =>
              if (keyboardInput.keysDown(Shift)) char.toUpper.toString
              else char.toString
            )
            .getOrElse("")
      }
      .mkString

  private def getInputState(canvas: Canvas): InputState = InputState(
    canvas.getPointerInput().position.map(pos => (pos.x, pos.y)),
    canvas.getPointerInput().isPressed,
    processKeyboard(canvas.getKeyboardInput())
  )

  private def renderUi(canvas: Canvas, renderOps: List[RenderOp]): Unit =
    renderOps.foreach {
      case RenderOp.DrawRect(Rect(x, y, w, h), color) =>
        canvas.fillRegion(x, y, w, h, MinartColor(color.r, color.g, color.b))
      case op: RenderOp.DrawText =>
        val font = 
          if (op.font.name == "bizcat") bizcat.withSize(op.font.fontSize)
          else unscii.withSize(op.font.fontSize)
        op.asDrawChars.foreach { case RenderOp.DrawChar(Rect(x, y, _, _), color, char) =>
          val charSprite =
            font.coloredChar(char, MinartColor(color.r, color.g, color.b))
          canvas
            .blit(charSprite, BlendMode.ColorMask(MinartColor(255, 0, 255)))(
              x,
              y
            )
        }
      case RenderOp.Custom(Rect(x, y, w, h), color, surface: Surface) =>
        canvas.blit(surface)(x, y, 0, 0, w, h)
      case RenderOp.Custom(Rect(x, y, w, h), color, data) =>
        canvas.fillRegion(x, y, w, h, MinartColor(color.r, color.g, color.b))
    }

  def run(canvasSettings: Canvas.Settings, getSettings: => state.Settings)(
      body: InputState => (List[RenderOp], _)
  ): Future[Unit] =
    AppLoop
      .statelessRenderLoop { (canvas: Canvas) =>
        val appSettings = getSettings
        if (appSettings.fullScreen != canvas.canvasSettings.fullScreen) {
          canvas.changeSettings(canvas.canvasSettings.copy(fullScreen = appSettings.fullScreen))
        }
        val inputState = getInputState(canvas)
        canvas.clear()
        val ops = body(inputState)._1
        renderUi(canvas, ops)
        val scanY = (System.currentTimeMillis() / 7) % canvas.height
        val postProcessed =
          if (appSettings.postProcess)
            canvas.view
              .flatMap((color) =>
                (x, y) =>
                  val scanLineSub = MinartColor.grayscale(15 * (y % 2))
                  val scanLineAdd = if (y >= scanY && y <= scanY + 30) MinartColor(0, 5, 0) else MinartColor(0, 0, 0)
                  color - scanLineSub + scanLineAdd
              )
          else canvas
        canvas.blit(postProcessed)(0, 0)
        canvas.redraw()
      }
      .configure(canvasSettings, LoopFrequency.hz60)
      .run()
