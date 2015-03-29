package tutorial.webapp.game

import scala.scalajs.js.annotation.JSExport

@JSExport
object ScratchPad extends Game{
  def handleKeyStrokes() = {
    import org.scalajs.dom

    var newestOpt: Option[Ball] = None

    dom.onkeypress = {(e: dom.KeyboardEvent) =>
      if(e.keyCode == 32) {
        val created = Ball(canvas)
        balls = balls :+ created
      }
      if(e.keyCode == 113) {
        balls = Seq.empty
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
}
