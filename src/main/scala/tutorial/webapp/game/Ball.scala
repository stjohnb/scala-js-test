package tutorial.webapp.game

import org.scalajs.dom._
import tutorial.webapp.common.RGB

import scala.util.Random

case class Ball(radius: Int = 5,
                colour: RGB = RGB(Random.nextInt(255), Random.nextInt(255),	Random.nextInt(255)),
                position: Point,
                velocity: Velocity,
                maxXy: Point) {
  def draw(ctx: CanvasRenderingContext2D): Unit = {
    ctx.fillStyle = colour.toString
    ctx.fillRect(position.x.toInt - radius, position.y.toInt - radius, radius * 2, radius * 2)
  }

  def move(): Ball = {
    val xCorrected = if (position.x > boxWidth || position.x < 0) {
      this.copy(velocity = this.velocity.copy(x = this.velocity.x * (smallRandom-1)))
    } else this

    val yCorrected = if (xCorrected.position.y > boxHeight || xCorrected.position.y < 0) {
      xCorrected.copy(velocity = xCorrected.velocity.copy(y = xCorrected.velocity.y * (smallRandom-1)))
    } else xCorrected

    yCorrected.copy(position = Point(yCorrected.position.x + yCorrected.velocity.x, yCorrected.position.y + yCorrected.velocity.y))
  }

  def smallRandom = (Random.nextDouble() - 0.5d) / 100

  def boxHeight = maxXy.x
  def boxWidth = maxXy.y
}

object Ball {

  val maxSpeed = 100

  def apply(maxXy: Point): Ball = {
    Ball(
      radius = Random.nextInt(5) + 1,
      colour = randomColour,
      position = Point(Random.nextInt(maxXy.x.toInt) /2, Random.nextInt(maxXy.y.toInt)) /2,
      velocity = Velocity(randomSpeed, randomSpeed),
      maxXy = maxXy
    )
  }

  def randomColour = RGB(Random.nextInt(255), Random.nextInt(255),	Random.nextInt(255))

  def randomSpeed: Double = (Random.nextInt(maxSpeed) - (maxSpeed / 2)) * Random.nextDouble()

  def apply(canvas: html.Canvas): Ball = {
    Ball(maxXy = Point(canvas.height, canvas.width))
  }
}

case class Point(x: Double, y: Double) {
  def +(p: Point) = Point(x + p.x, y + p.y)
  def /(d: Double) = Point(x / d, y / d)
}

case class Velocity(x: Double, y: Double) {
  def /(d: Double) = Velocity(x / d, y / d)
}

