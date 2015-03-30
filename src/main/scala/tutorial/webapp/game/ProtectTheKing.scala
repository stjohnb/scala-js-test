package tutorial.webapp.game

import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, html}
import tutorial.webapp.common._

import scala.scalajs.js.annotation.JSExport
@JSExport
object ProtectTheKing extends Game {

  lazy val kingR = 10
  lazy val pawnR = 30
  override lazy val coefficientOfRestitution: Double = 1d
  override lazy val acceleration: Double = 0.98d
  lazy val maxVelocity = 15

  lazy val startingFraction = 6
  lazy val team1 = Seq(
    ball(position = Vector((canvas.width / startingFraction) - (pawnR +1), canvas.height /2), colour = RGB.blue, radius = kingR),
    ball(position = Vector(canvas.width / startingFraction, (canvas.height /2) + (2 * pawnR + 1)), colour = RGB.blue, radius = pawnR),
    ball(position = Vector((canvas.width / startingFraction) + (pawnR +1), canvas.height /2), colour = RGB.blue, radius = pawnR),
    ball(position = Vector(canvas.width / startingFraction, (canvas.height /2) - (2 * pawnR + 1)), colour = RGB.blue, radius = pawnR)
  )

  lazy val team2 = Seq(
    ball(position = Vector(canvas.width - ((canvas.width / startingFraction) - (pawnR +1)), canvas.height /2), colour = RGB.green, radius = kingR),
    ball(position = Vector(canvas.width - (canvas.width / startingFraction), (canvas.height /2) - (2 * pawnR + 1)), colour = RGB.green, radius = pawnR),
    ball(position = Vector(canvas.width - ((canvas.width / startingFraction) + (pawnR +1)), canvas.height /2), colour = RGB.green, radius = pawnR),
    ball(position = Vector(canvas.width - (canvas.width / startingFraction), (canvas.height /2) + (2 * pawnR + 1)), colour = RGB.green, radius = pawnR)
  )

  //height and with here are canvas heights and widths... not real life goal width
  lazy val halfGoalHeight = canvas.height / 10
  lazy val goalWidth = canvas.width / 50
  lazy val blueGoal = RectangleDelta(0, (canvas.height /2) - halfGoalHeight, goalWidth, halfGoalHeight * 2)
  lazy val greenGoal = RectangleDelta(canvas.width - goalWidth, (canvas.height /2) - halfGoalHeight, goalWidth, halfGoalHeight * 2)

  var selected: Option[Ball] = None
  var currentCoordinates: Option[Vector] = None

  override def initialBalls: Seq[Ball] = team1 ++ team2

  override def clear(canvas: html.Canvas): Unit = {
    super.clear(canvas)
    val ctx = context(canvas)

    ctx.fillStyle = "red"

    ctx.fillRect(blueGoal.x, blueGoal.y, blueGoal.deltaX, blueGoal.deltaY)
    ctx.fillRect(greenGoal.x, greenGoal.y, greenGoal.deltaX, greenGoal.deltaY)
  }

  override def handleKeyStrokes(): Unit = {
    dom.onkeypress = { e: dom.KeyboardEvent =>
      e.keyCode match {
        case 32 => currentAction = Some(dom.setInterval(() => {run(); draw()}, timeStep))
        case i => println(s"Keypress: $i")
      }
    }
    dom.onmousedown = { e: dom.MouseEvent =>
      val cursor = new Ball(
        radius = 15,
        position = Vector(e.clientX, e.clientY),
        maxXy = Vector(canvas.height, canvas.width)
      )
      selected = balls.find(cursor.touching)
      currentAction = Some(dom.setInterval(() => {this.draw()}, timeStep))
    }
    dom.onmouseup = { e: dom.MouseEvent =>
      selected.foreach { b =>
        b.velocity = Vector(e.clientX - b.position.x, e.clientY - b.position.y).unit * maxVelocity
        println(s"Velocity: ${b.velocity}")
        selected = None
        currentCoordinates = None
        draw()
      }
    }
    dom.onmousemove = { e: dom.MouseEvent =>
      selected.foreach { _ =>
        currentCoordinates = Some(Vector(e.clientX, e.clientY))
      }
    }
  }

  override def draw(): Unit = {
    val ctx = context(canvas)
    clear(canvas)
    balls.foreach{ b =>
      b.draw(ctx)
      if (!inTurn) drawArrow(from = b.position, arrow = b.velocity * 10)(ctx)
    }

    for {
      origin <- selected.map(_.position)
      destination <- currentCoordinates
    } {
      val intendedVelocity = destination - origin

      val capped = if(intendedVelocity.magnitude > maxVelocity * 10) intendedVelocity.unit * maxVelocity * 10
      else intendedVelocity
      
      drawArrow(from = origin, arrow = capped)(context(canvas))
    }
  }
  
  def drawArrow(from: Vector, arrow: Vector)(ctx: CanvasRenderingContext2D): Unit = {
    ctx.beginPath()
    ctx.strokeStyle = RGB.red.toString
    ctx.lineWidth = 5
    ctx.moveTo(from.x, from.y)
    ctx.lineTo(from.x + arrow.x, from.y + arrow.y)
    ctx.stroke()
  }

  override def run(): Unit = {
    super.run()

    balls = balls.filterNot(b => blueGoal.contains(b.position) || greenGoal.contains(b.position))
  }

  def ball(radius: Int = 5, position: Vector, colour: RGB): Ball = {
    new Ball(
      radius = radius,
      colour = colour,
      position = position,
      velocity = Vector(0,0),
      maxXy = Vector(canvas.height, canvas.width)
    )
  }
}

case class Arrow(from: Vector, to: Vector)