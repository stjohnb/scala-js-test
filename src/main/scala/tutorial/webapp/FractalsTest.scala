package tutorial.webapp

import org.scalajs.dom
import org.scalajs.dom.html

import scala.scalajs.js.annotation.JSExport
import scala.util.Random

@JSExport
object FractalsTest {
  @JSExport
  def main(canvas: html.Canvas): Unit = {

    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    val squareSize = 800

    var p = IPoint(0,0)
    val corners = Seq(
      IPoint(squareSize,squareSize),
      IPoint(0,squareSize),
      IPoint(squareSize / 2,0)
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

case class IPoint(x: Int, y: Int) {
  def +(p: IPoint) = IPoint(x + p.x, y + p.y)
  def /(d: Int) = IPoint(x / d, y / d)
}

case class RGB(r: Int, g: Int, b: Int) {
  override def toString = s"rgb($r, $g, $b)"
}

object RGB {
  def apply(p: IPoint, squareSize: Int): RGB = {
    val height = 512.0 / (squareSize + p.y)
    val r = (p.x * height).toInt
    val g = ((squareSize-p.x)*height).toInt
    val b = p.y

    RGB(r, g, b)
  }
}

