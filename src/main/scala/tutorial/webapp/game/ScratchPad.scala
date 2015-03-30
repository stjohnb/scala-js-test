package tutorial.webapp.game

import tutorial.webapp.common.{Ball, Vector}
import scala.scalajs.js.annotation.JSExport

@JSExport
object ScratchPad extends Game{
  def handleKeyStrokes() = {
    import org.scalajs.dom

    var newestOpt: Option[Ball] = None

    dom.onkeypress = { e: dom.KeyboardEvent =>
      e.keyCode match {
        case 32 => balls = balls :+ Ball(canvas)
        case 113 => pauseDrawing()
        case _ =>
      }
    }
    dom.onmousedown = { e: dom.MouseEvent =>
      val ball = Ball(canvas)
      ball.position = Vector(e.clientX, e.clientY)
      ball.velocity = Vector(0,0)
      balls = balls :+ ball
      newestOpt = Some(ball)
    }
    dom.onmouseup = { e: dom.MouseEvent =>
      newestOpt.foreach { newest =>
        newest.velocity = Vector(e.clientX - newest.position.x, e.clientY - newest.position.y) / 10

        newestOpt = None
      }
    }
  }

  override def initialBalls: Seq[Ball] = Seq.empty

  override def draw(): Unit  = {
    val ctx = context(canvas)
    clear(canvas)
    balls.foreach(_.draw(ctx))
  }
}
