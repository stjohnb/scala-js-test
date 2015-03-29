package tutorial.webapp.common

import org.scalajs.dom._
import org.scalajs.dom.html._
import scala.util.Random

class Ball(val radius: Int = 5,
                colour: RGB = RGB(Random.nextInt(255), Random.nextInt(255),	Random.nextInt(255)),
                var position: Vector,
                var velocity: Vector = Vector (0, 0),
                maxXy: Vector) {
  def draw(ctx: CanvasRenderingContext2D): Unit = {
    ctx.beginPath()
    ctx.arc(position.x, position.y, radius, 0, 2* Math.PI, anticlockwise = false)
    ctx.fillStyle = colour.toString
    ctx.fill()
  }

  def move(acceleration: Double)(canvas: Canvas): Unit = {
    if (position.x > canvas.width || position.x < 0) {
      velocity = velocity.copy(x = velocity.x * (smallRandom - 1))
    }

    if (position.y > canvas.height || position.y < 0) {
      velocity = velocity.copy(y = velocity.y * (smallRandom - 1))
    }

    velocity = velocity * acceleration

    position = Vector(position.x + velocity.x, position.y + velocity.y)
  }

  def smallRandom = (Random.nextDouble() - 0.5d) / 100

  lazy val mass = (4d / 3d) * Math.PI * radius * radius

  def momentumX = mass * velocity.x
  def momentumY = mass * velocity.y

  def touching(other: Ball): Boolean = {
    val rSum = this.radius + other.radius

    val dx = Math.abs(this.position.x - other.position.x)
    if (dx <= rSum) {
      val dy = Math.abs(this.position.y - other.position.y)
      if (dy <= rSum) {
        val d = Math.sqrt(dx * dx + dy * dy)
        if (d < rSum) {
          true
        }else false
      }else false
    }else false
  }

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




