package net.bstjohn.kwyjibo.web.game

import net.bstjohn.kwyjibo.web.common.Ball
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.Canvas

import scala.scalajs.js.annotation.JSExport

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

  protected var inTurn = false
  protected var count = 0
  
  @JSExport
  def readyTurn() = {
    inTurn = true
    currentAction = Some(dom.setInterval(() => {run(); this.draw()}, timeStep))
  }

  def run(): Unit = {
    count += 1
    if(count % turnLength == 0) {
      inTurn = false
      pauseDrawing()
    }else{
      handleCollisions()
      balls.foreach { b => b.move(acceleration)(canvas) }
      draw()
    }
  }

  def init(): Unit = { draw() }

  def pauseDrawing(): Unit = {
    currentAction.foreach(dom.clearInterval)
    currentAction = None
  }

  def handleCollisions() = {
    for {
      i <- 0 to balls.size - 1
      j <- i + 1 to balls.size - 1
    } Ball.collideIfNecessary(balls(i), balls(j))(coefficientOfRestitution)
  }

  def draw(): Unit

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
