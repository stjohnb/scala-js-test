package tutorial.webapp

import org.scalajs.dom
import org.scalajs.dom.html

import scala.scalajs.js.annotation.JSExport

@JSExport
object Game {

  @JSExport
  def main(canvas: html.Canvas) = {
    resizeCanvas(canvas)
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

  def fill(canvas: html.Canvas): Unit = {
    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    ctx.fillStyle = "black"
    ctx.fillRect(0,0,canvas.width,canvas.height)
  }
}
