package tutorial.webapp.game

import org.scalajs.dom._
import tutorial.webapp.common.RGB

import scala.util.Random

class Ball(val radius: Int = 5,
                colour: RGB = RGB(Random.nextInt(255), Random.nextInt(255),	Random.nextInt(255)),
                var position: Vector,
                var velocity: Vector,
                maxXy: Vector) {
  def draw(ctx: CanvasRenderingContext2D): Unit = {
    ctx.fillStyle = colour.toString
    ctx.fillRect(position.x.toInt - radius, position.y.toInt - radius, radius * 2, radius * 2)
  }

  def move(): Unit = {
    if (position.x > boxWidth || position.x < 0) {
      velocity = velocity.copy(x = velocity.x * (smallRandom - 1))
    }

    if (position.y > boxHeight || position.y < 0) {
      velocity = velocity.copy(y = velocity.y * (smallRandom - 1))
    }

    velocity = velocity * 0.999

    position = Vector(position.x + velocity.x, position.y + velocity.y)
  }

  def smallRandom = (Random.nextDouble() - 0.5d) / 100

  def boxHeight = maxXy.x
  def boxWidth = maxXy.y

  lazy val mass = (4d / 3d) * Math.PI * radius * radius

  def momentumX = mass * velocity.x
  def momentumY = mass * velocity.y

  override def toString = s"Ball: Radius: $radius Position: (${position.x}, ${position.y}), Velocity: (${velocity.x}, ${velocity.y})"
}

object Ball {

  val maxSpeed = 100

  def apply(maxXy: Vector): Ball = {
    new Ball(
      radius = Random.nextInt(5) + 1,
      colour = randomColour,
      position = Vector(Random.nextInt(maxXy.x.toInt) /2, Random.nextInt(maxXy.y.toInt)) /2,
      velocity = Vector(randomSpeed, randomSpeed),
      maxXy = maxXy
    )
  }

  def randomColour = RGB(Random.nextInt(255), Random.nextInt(255),	Random.nextInt(255))

  def randomSpeed: Double = (Random.nextInt(maxSpeed) - (maxSpeed / 2)) * Random.nextDouble()

  def apply(canvas: html.Canvas): Ball = {
    Ball(maxXy = Vector(canvas.height, canvas.width))
  }
}

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



