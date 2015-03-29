package tutorial.webapp.common

case class RectangleDelta(x: Int, y: Int, deltaX: Int, deltaY: Int) {
  def contains(point: Vector): Boolean = {
    this.toRectanglePoints.contains(point)
  }
  def toRectanglePoints: RectanglePoints = {
    RectanglePoints(x, y, x + deltaX, y + deltaY)
  }
}

case class RectanglePoints(x1: Int, y1: Int, x2: Int, y2: Int) {
  def contains(point: Vector): Boolean = {
    point.x > x1 && point.x < x2 && point.y > y1 && point.y < y2
  }
}