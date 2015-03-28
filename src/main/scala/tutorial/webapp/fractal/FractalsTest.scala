package tutorial.webapp.fractal

import org.scalajs.dom
import org.scalajs.dom.html
import tutorial.webapp.common.RGB

import scala.scalajs.js.annotation.JSExport
import scala.util.Random

@JSExport
object FractalsTest {
  @JSExport
  def main(canvas: html.Canvas): Unit = {

    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    val squareSize = 800

    var p = Point(0,0)
    val corners = Seq(
      Point(squareSize,squareSize),
      Point(0,squareSize),
      Point(squareSize / 2,0)
    )

    ctx.fillStyle = "black"
    ctx.fillRect(0,0,squareSize,squareSize)

    def run() = for (i <- 0 until 10){
      p = (p + corners(Random.nextInt(3))) / 2

      ctx.fillStyle = RGB(p, squareSize).toString
      ctx.fillRect(p.x, p.y, 1, 1)
    }

    dom.setInterval(()=> run(), timeout = 1)
  }
}

case class Point(x: Int, y: Int) {
  def +(p: Point) = Point(x + p.x, y + p.y)
  def /(d: Int) = Point(x / d, y / d)
}
