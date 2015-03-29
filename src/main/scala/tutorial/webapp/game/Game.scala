package tutorial.webapp.game

import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html.Canvas

import scala.scalajs.js.annotation.JSExport
import tutorial.webapp.common.{Ball, Vector}

trait Game {

  def initialBalls: Seq[Ball]
  def handleKeyStrokes(): Unit

  lazy val timeStep: Double = 2d
  lazy val coefficientOfRestitution: Double = 0.8d
  lazy val acceleration: Double = 0.99
  lazy val turnLength = 300

  def canvas: Canvas = dom.document.getElementById("canvas").asInstanceOf[html.Canvas]

  protected var balls = Seq.empty[Ball]

  protected var currentAction: Option[Int] = None

  var count = 0

  def run(): Unit = {
    count += 1
    if(count % turnLength == 0) pause()
    handleCollisions()
    balls.foreach { b => b.move(acceleration)(canvas) }
  }

  def init(): Unit = {
    val action = dom.setInterval(() => {run(); draw()}, timeStep)
    currentAction = Some(action)
  }

  def pause(): Unit = currentAction.foreach(dom.clearInterval)

  def handleCollisions() = {
    for {
      i <- 0 to balls.size - 1
      j <- i + 1 to balls.size - 1
    } collideIfNecessary(balls(i), balls(j))

    def collideIfNecessary(b1: Ball, b2: Ball): Unit = {
      if (b1.touching(b2)) performCollision()


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
    balls = initialBalls
    init()
  }

  def clear(canvas: html.Canvas): Unit = {
    val ctx = context(canvas)

    ctx.fillStyle = "black"
    ctx.fillRect(0,0,canvas.width,canvas.height)
  }

  def context(canvas: html.Canvas) = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
}
