package tutorial.webapp

import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, html}

import scala.scalajs.js.annotation.JSExport
import scala.util.Random

case class Ball(radius: Int = 5, colour: RGB, position: Point, velocity: Point, maxXy: Point) {
  def draw(ctx: CanvasRenderingContext2D): Unit = {
    ctx.fillStyle = colour.toString
    ctx.fillRect(position.x - radius, position.y - radius, radius * 2, radius * 2)
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
      position = Point(Random.nextInt(maxXy.x), Random.nextInt(maxXy.y)),
      velocity = Point(Random.nextInt(maxSpeed) - (maxSpeed / 2), Random.nextInt(maxSpeed) - (maxSpeed / 2)),
      maxXy = maxXy
    )
  }

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
    balls = for (i <- 1 until 100) yield Ball(canvas)
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

    dom.onkeypress = {(e: dom.KeyboardEvent) =>
      if(e.keyCode == 32) balls = balls :+ Ball(canvas)
      if(e.keyCode == 113) {
        println(s"Clearing")
        balls = Seq.empty
      }
    }
  }
}
