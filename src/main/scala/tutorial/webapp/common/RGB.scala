package tutorial.webapp.common

import tutorial.webapp.fractal.Point

case class RGB(r: Int, g: Int, b: Int) {
  override def toString = s"rgb($r, $g, $b)"
}

object RGB {
  def apply(p: Point, squareSize: Int): RGB = {
    val height = 512.0 / (squareSize + p.y)
    val r = (p.x * height).toInt
    val g = ((squareSize-p.x)*height).toInt
    val b = p.y

    RGB(r, g, b)
  }
}

