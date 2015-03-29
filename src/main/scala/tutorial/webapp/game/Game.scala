package tutorial.webapp.game

import org.scalajs.dom
import org.scalajs.dom.html

import scala.scalajs.js.annotation.JSExport

@JSExport
object Game {

  var count = 0
  val timeStep = 2d

  val canvas = dom.document.getElementById("canvas").asInstanceOf[html.Canvas]

  var balls: Seq[Ball] = Seq.empty

  def run() = {
    count += 1
    handleCollisions()
    balls.foreach { b => b.move() }
  }

  def handleCollisions() = {
    for {
      i <- 0 to balls.size - 1
      j <- i + 1 to balls.size - 1
    } collideIfNecessary(balls(i), balls(j))
  }

  def collideIfNecessary(b1: Ball, b2: Ball): Unit = {
    val rSum = b1.radius + b2.radius

    val dx = Math.abs(b1.position.x - b2.position.x)
    if(dx <= rSum)  {
      val dy = Math.abs(b1.position.y - b2.position.y)
      if(dy <= rSum) {
        val d = Math.sqrt(dx * dx + dy * dy)
        if(d < rSum){
          performCollision()
        }
      }
    }

    def performCollision() = {
      //Find the unit normal and the unit tangent
      val unitNormal = Vector(b1.position.x - b2.position.x, b1.position.y - b2.position.y).unit
      val unitTangent = Vector(-1 * unitNormal.y, unitNormal.x)

      //Project velocities onto the unit normal and unit tangent
      val v1n = unitNormal dotProduct b1.velocity
      val v1t = unitTangent dotProduct b1.velocity
      val v2n = unitNormal dotProduct b2.velocity
      val v2t = unitTangent dotProduct b2.velocity

      //Tangential velocities are not changed
      val v1tPrimeScalar = v1t
      val v2tPrimeScalar = v2t

      //Normal velocities obey 1d collision formulae
      val v1nPrimeScalar = (v1n * (b1.mass - b2.mass) + 2 * b2.mass * v2n) / (b1.mass + b2.mass)
      val v2nPrimeScalar = (v2n * (b2.mass - b1.mass) + 2 * b1.mass * v1n) / (b1.mass + b2.mass)

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



  def draw() = {
    val ctx = context(canvas)
    clear(canvas)
    balls.foreach(_.draw(ctx))
  }

  @JSExport
  def resizeCanvas(canvas: html.Canvas) = {
    import org.scalajs.dom

    canvas.width = dom.window.innerWidth - 20
    canvas.height = dom.window.innerHeight - 20

    clear(canvas)
  }

  @JSExport
  def main() = {
    resizeCanvas(canvas)
    clear(canvas)

    handleKeyStrokes()
    dom.setInterval(() => {run(); draw()}, timeStep)
  }

  def clear(canvas: html.Canvas): Unit = {
    val ctx = context(canvas)

    ctx.fillStyle = "black"
    ctx.fillRect(0,0,canvas.width,canvas.height)
  }

  def context(canvas: html.Canvas) = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  def handleKeyStrokes() = {
    import org.scalajs.dom

    var newestOpt: Option[Ball] = None

    dom.onkeypress = {(e: dom.KeyboardEvent) =>
      if(e.keyCode == 32) {
        val created = Ball(canvas)
        balls = balls :+ created
      }
      if(e.keyCode == 113) {
        balls = Seq.empty
      }
    }
    dom.onmousedown = { e: dom.MouseEvent =>
      val ball = Ball(canvas)
      ball.position = Vector(e.clientX, e.clientY)
      ball.velocity = Vector(0,0)
      balls = balls :+ ball
      newestOpt = Some(ball)
    }
    dom.onmouseup = { e: dom.MouseEvent =>
      newestOpt.foreach { newest =>
        newest.velocity = Vector(e.clientX - newest.position.x, e.clientY - newest.position.y) / 10

        newestOpt = None
      }
    }
  }
}
