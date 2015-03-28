package tutorial.webapp

import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, html}

import scala.scalajs.js.annotation.JSExport
import scala.util.Random

case class Ball(radius: Int = 5,
                colour: RGB = RGB(Random.nextInt(255), Random.nextInt(255),	Random.nextInt(255)),
                position: Point,
                velocity: Point,
                maxXy: Point) {
  def draw(ctx: CanvasRenderingContext2D): Unit = {
    ctx.fillStyle = colour.toString
    ctx.fillRect(position.x.toInt - radius, position.y.toInt - radius, radius * 2, radius * 2)
  }

  def move(): Ball = {
    val xCorrected = if (position.x > boxWidth || position.x < 0) {
      this.copy(velocity = this.velocity.copy(x = this.velocity.x * -1))
    } else this

    val yCorrected = if (xCorrected.position.y > boxHeight || xCorrected.position.y < 0) {
      xCorrected.copy(velocity = xCorrected.velocity.copy(y = xCorrected.velocity.y * -1))
    } else xCorrected

    yCorrected.copy(position = yCorrected.position + yCorrected.velocity)
  }

  def boxHeight = maxXy.x
  def boxWidth = maxXy.y
}

object Ball {

  val maxSpeed = 100

  def apply(maxXy: Point): Ball = {
    Ball(
      radius = Random.nextInt(5),
      colour = RGB(Random.nextInt(255), Random.nextInt(255),	Random.nextInt(255)),
      position = Point(Random.nextInt(maxXy.x.toInt), Random.nextInt(maxXy.y.toInt)),
      velocity = Point(randomSpeed, randomSpeed),
      maxXy = maxXy
    )
  }

  def randomSpeed: Double = (Random.nextInt(maxSpeed) - (maxSpeed / 2)) * Random.nextDouble()

  def apply(canvas: html.Canvas): Ball = {
    Ball(maxXy = Point(canvas.height, canvas.width))
  }
}

@JSExport
object Game {

  var count = 0
  val timestep = 2d

  val canvas = dom.document.getElementById("canvas").asInstanceOf[html.Canvas]

  var balls: Seq[Ball] = Seq.empty

  def run() = {
    count += 1
    balls = balls.map { _.move() }
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
    dom.setInterval(() => {run(); draw()}, timestep)
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
          velocity = Point(e.clientX - start.x, e.clientY - start.y)
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