package tutorial.webapp.common

case class Vector(x: Double, y: Double) {
  def +(p: Vector) = Vector(x + p.x, y + p.y)
  def -(p: Vector) = Vector(x - p.x, y - p.y)

  def /(d: Double) = Vector(x / d, y / d)
  def *(d: Double) = Vector(x * d, y * d)

  def dotProduct(other: Vector): Double = {
    this.x * other.x + this.y * other.y
  }

  def magnitude: Double = Math.sqrt(x*x + y*y)

  def unit: Vector = this / magnitude
}
