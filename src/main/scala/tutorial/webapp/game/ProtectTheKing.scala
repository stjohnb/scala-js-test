package tutorial.webapp.game

import org.scalajs.dom
import org.scalajs.dom.html
import tutorial.webapp.common.RGB

import scala.scalajs.js.annotation.JSExport

@JSExport
object ProtectTheKing extends Game {

  lazy val kingR = 30
  lazy val pawnR = 10

  lazy val team1 = Seq(
    ball(position = Vector(200, 260), colour = RGB.blue, radius = kingR),
    ball(position = Vector(270, 200), colour = RGB.blue, radius = pawnR),
    ball(position = Vector(340, 260), colour = RGB.blue, radius = pawnR),
    ball(position = Vector(270, 320), colour = RGB.blue, radius = pawnR)
  )

  lazy val team2 = Seq(
    ball(position = Vector(1200, 260), colour = RGB.green, radius = kingR),
    ball(position = Vector(1120, 200), colour = RGB.green, radius = pawnR),
    ball(position = Vector(1040, 260), colour = RGB.green, radius = pawnR),
    ball(position = Vector(1120, 320), colour = RGB.green, radius = pawnR)
  )

  var selected: Option[Ball] = None

  override def initialBalls: Seq[Ball] = team1 ++ team2

  override def clear(canvas: html.Canvas): Unit = {
    super.clear(canvas)
    val ctx = context(canvas)

    ctx.fillStyle = "red"
    //ctx.fillRect(0,0,canvas.width,canvas.height)
  }

  override def handleKeyStrokes(): Unit = {
    dom.onkeypress = { e: dom.KeyboardEvent =>
      e.keyCode match {
        case 113 => pause()
      }
    }
    dom.onmousedown = { e: dom.MouseEvent =>
      val cursor = new Ball(
        radius = 15,
        position = Vector(e.clientX, e.clientY),
        maxXy = Vector(canvas.height, canvas.width)
      )
      selected = balls.find(cursor.touching)
    }
    dom.onmouseup = { e: dom.MouseEvent =>
      selected.foreach { b =>
        b.velocity = Vector(e.clientX - b.position.x, e.clientY - b.position.y) / 10
        println(s"Set velocity ${b.velocity}")
        selected = None
      }
    }
  }

  def ball(radius: Int = 5, position: Vector, colour: RGB): Ball = {
    new Ball(
      radius = radius,
      colour = colour,
      position = position,
      velocity = Vector(0,0),
      maxXy = Vector(canvas.height, canvas.width)
    )
  }
}
