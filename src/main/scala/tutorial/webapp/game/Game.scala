package tutorial.webapp.game

import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.Canvas

import scala.scalajs.js.annotation.JSExport
import tutorial.webapp.common.{Ball, Vector}

trait Game {

  def initialBalls: Seq[Ball]
  def handleKeyStrokes(): Unit

  lazy val timeStep: Double = 2d
  lazy val coefficientOfRestitution: Double = 0.8d
  lazy val acceleration: Double = 0.99
  lazy val turnLength = 300

  def canvas: Canvas = dom.document.getElementById("canvas").asInstanceOf[html.Canvas]

  protected var balls = Seq.empty[Ball]

  protected var currentAction: Option[Int] = None

  var count = 0

  def run(): Unit = {
    count += 1
    if(count % turnLength == 0) pause()
    handleCollisions()
    balls.foreach { b => b.move(acceleration)(canvas) }
  }

  def init(): Unit = {
    val action = dom.setInterval(() => {run(); draw()}, timeStep)
    currentAction = Some(action)
  }

  def pause(): Unit = currentAction.foreach(dom.clearInterval)

  def handleCollisions() = {
    for {
      i <- 0 to balls.size - 1
      j <- i + 1 to balls.size - 1
    } Ball.collideIfNecessary(balls(i), balls(j))(coefficientOfRestitution)
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
    balls = initialBalls
    init()
  }

  def clear(canvas: html.Canvas): Unit = {
    val ctx = context(canvas)

    ctx.fillStyle = "black"
    ctx.fillRect(0,0,canvas.width,canvas.height)
  }

  def context(canvas: html.Canvas) = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
}
