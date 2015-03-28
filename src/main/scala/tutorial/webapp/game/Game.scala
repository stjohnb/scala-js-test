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
    balls = balls.map { _.move() }
    if(count % 1000 == 0 ) balls.foreach(println)
  }

  def draw() = {
    val ctx = context(canvas)
    balls.foreach(_.draw(ctx))
  }

  @JSExport
  def resizeCanvas(canvas: html.Canvas) = {
    import org.scalajs.dom

    println(s"resize w: ${canvas.width} = ${dom.window.innerWidth}")
    println(s"resize h: ${canvas.height} = ${dom.window.innerHeight}")

    canvas.width = dom.window.innerWidth - 20
    canvas.height = dom.window.innerHeight - 20

    fill(canvas)
  }

  @JSExport
  def main() = {
    resizeCanvas(canvas)
    fill(canvas)

    handleKeyStrokes()
    dom.setInterval(() => {run(); draw()}, timeStep)
  }

  def fill(canvas: html.Canvas): Unit = {
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
        println(s"Created $created")
        balls = balls :+ created
      }
      if(e.keyCode == 113) {
        println(s"Clearing")
        balls = Seq.empty
      }
    }
    dom.onmousedown = { e: dom.MouseEvent =>
      mouseDown = Some(Point(e.clientX, e.clientY))
      println(s"MouseDown $mouseDown")
    }
    dom.onmouseup = { e: dom.MouseEvent =>
      mouseDown.map { start =>
        val ball = Ball(canvas).copy(
          position = start,
          velocity = Velocity(e.clientX - start.x, e.clientY - start.y)
        )
        println(s"MouseUp $ball")
        mouseDown = None
        balls :+ ball
      }
    }
  }
}

case class Point(x: Double, y: Double) {
  def +(p: Point) = Point(x + p.x, y + p.y)
  def /(d: Double) = Point(x / d, y / d)
}

case class Velocity(x: Double, y: Double)