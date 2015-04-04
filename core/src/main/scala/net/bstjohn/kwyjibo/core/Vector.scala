package net.bstjohn.kwyjibo.core

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

object Vector {

  def unitNormalAndTangent(v1: Vector, v2: Vector): (Vector, Vector) = {
    val unitNormal = Vector(v1.x - v2.x, v1.y - v2.y).unit
    val unitTangent = Vector(-1 * unitNormal.y, unitNormal.x)
    (unitNormal, unitTangent)
  }
}