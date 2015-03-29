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

  def collideIfNecessary(b1: Ball, b2: Ball)(coefficientOfRestitution: Double): Unit = {
    if (b1.touching(b2) && gettingCloser()) {
      performCollision()
    }

    def gettingCloser(): Boolean = {
      val currentDistance = (b1.position - b2.position).magnitude
      val p1Next = Vector(b1.position.x + b1.velocity.x, b1.position.y + b1.velocity.y)
      val p2Next = Vector(b2.position.x + b2.velocity.x, b2.position.y + b2.velocity.y)

      val nextDistance = (p1Next - p2Next).magnitude

      nextDistance < currentDistance
    }

    def performCollision() = {
      //Find the unit normal and the unit tangent
      val (unitNormal, unitTangent) = Vector.unitNormalAndTangent(b1.position, b2.position)

      //Project velocities onto the unit normal and unit tangent
      val v1n = unitNormal dotProduct b1.velocity
      val v2n = unitNormal dotProduct b2.velocity
      val v1t = unitTangent dotProduct b1.velocity
      val v2t = unitTangent dotProduct b2.velocity

      //Tangential velocities are not changed
      val v1tPrimeScalar = v1t
      val v2tPrimeScalar = v2t

      //Normal velocities obey 1d collision formulae
      val v1nPrimeScalar = (coefficientOfRestitution * b2.mass * (v2n - v1n) + b1.mass * v1n + b2.mass * v2n) / (b1.mass + b2.mass)
      val v2nPrimeScalar = (coefficientOfRestitution * b1.mass * (v1n - v2n) + b2.mass * v2n + b1.mass * v1n) / (b1.mass + b2.mass)

      //Convert scalars into vectors
      val v1nPrime = unitNormal * v1nPrimeScalar
      val v1tPrime = unitTangent * v1tPrimeScalar

      val v2nPrime = unitNormal * v2nPrimeScalar
      val v2tPrime = unitTangent * v2tPrimeScalar

      //Final velocities are the sums of normal and tangential velocities
      b1.velocity = v1nPrime + v1tPrime
      b2.velocity = v2nPrime + v2tPrime
    }
  }
}




