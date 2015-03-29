package tutorial.webapp.game

import org.scalajs.dom
import org.scalajs.dom.html
import tutorial.webapp.common.{RectangleDelta, Ball, RGB, Vector}

import scala.scalajs.js.annotation.JSExport
@JSExport
object ProtectTheKing extends Game {

  lazy val kingR = 10
  lazy val pawnR = 30

  lazy val team1 = Seq(
    ball(position = Vector(200, canvas.height /2), colour = RGB.blue, radius = kingR),
    ball(position = Vector(270, (canvas.height /2) - (2 * pawnR)), colour = RGB.blue, radius = pawnR),
    ball(position = Vector(340, canvas.height /2), colour = RGB.blue, radius = pawnR),
    ball(position = Vector(270, (canvas.height /2) + (2 * pawnR)), colour = RGB.blue, radius = pawnR)
  )

  lazy val team2 = Seq(
    ball(position = Vector(1200, canvas.height /2), colour = RGB.green, radius = kingR),
    ball(position = Vector(1120, (canvas.height /2) - (2 * pawnR)), colour = RGB.green, radius = pawnR),
    ball(position = Vector(1040, canvas.height /2), colour = RGB.green, radius = pawnR),
    ball(position = Vector(1120, (canvas.height /2) + (2 * pawnR)), colour = RGB.green, radius = pawnR)
  )

  //height and with here are canvas heights and widths... not real life goal width
  lazy val halfGoalHeight = canvas.height / 10
  lazy val goalWidth = canvas.width / 50
  lazy val blueGoal = RectangleDelta(0, (canvas.height /2) - halfGoalHeight, goalWidth, halfGoalHeight * 2)
  lazy val greenGoal = RectangleDelta(canvas.width - goalWidth, (canvas.height /2) - halfGoalHeight, goalWidth, halfGoalHeight * 2)

  var selected: Option[Ball] = None

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
        case 113 => {
          println(s"Pausing: $currentAction")
          pause()
        }
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
    }
    dom.onmouseup = { e: dom.MouseEvent =>
      selected.foreach { b =>
        b.velocity = Vector(e.clientX - b.position.x, e.clientY - b.position.y) / 10
        selected = None
      }
    }
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

  override def init(): Unit = {
    draw()
  }
}
