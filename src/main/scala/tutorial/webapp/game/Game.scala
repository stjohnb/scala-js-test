package tutorial.webapp.game

import org.scalajs.dom
import org.scalajs.dom.html

import scala.scalajs.js.annotation.JSExport

@JSExport
object Game {

  var count = 0
  val timeStep = 2d

  val canvas = dom.document.getElementById("canvas").asInstanceOf[html.Canvas]

  var balls: Seq[Ball] = Seq.empty

  def run() = {
    count += 1
    handleCollisions()
    balls.foreach { b => b.move() }
  }

  def handleCollisions() = {
    for {
      i <- 0 to balls.size - 1
      j <- i + 1 to balls.size - 1
    } collide(balls(i), balls(j))
  }

  def collide(b1: Ball, b2: Ball): Unit = {
    val rSum = b1.radius + b2.radius

    val dx = Math.abs(b1.position.x - b2.position.x)
    if(dx <= rSum)  {
      val dy = Math.abs(b1.position.y - b2.position.y)
      if(dy <= rSum) {
        val d = Math.sqrt(dx * dx + dy * dy)
        if(d < rSum){
          b1.velocity = b1.velocity * -1
          b2.velocity = b2.velocity * -1
        }
      }
    }
  }

  def draw() = {
    val ctx = context(canvas)
    clear(canvas)
    balls.foreach(_.draw(ctx))
  }

  @JSExport
  def resizeCanvas(canvas: html.Canvas) = {
    import org.scalajs.dom

    canvas.width = dom.window.innerWidth - 20
    canvas.height = dom.window.innerHeight - 20

    clear(canvas)
  }

  @JSExport
  def main() = {
    resizeCanvas(canvas)
    clear(canvas)

    handleKeyStrokes()
    dom.setInterval(() => {run(); draw()}, timeStep)
  }

  def clear(canvas: html.Canvas): Unit = {
    val ctx = context(canvas)

    ctx.fillStyle = "black"
    ctx.fillRect(0,0,canvas.width,canvas.height)
  }

  def context(canvas: html.Canvas) = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  def handleKeyStrokes() = {
    import org.scalajs.dom

    var mouseDown: Option[Point] = None

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
      mouseDown = Some(Point(e.clientX, e.clientY))
    }
    dom.onmouseup = { e: dom.MouseEvent =>
      mouseDown.foreach { start =>
        val ball = Ball(canvas)

        ball.position = start
        ball.velocity = Velocity(e.clientX - start.x, e.clientY - start.y) / 10

        mouseDown = None
        balls = balls :+ ball
      }
    }
  }
}
