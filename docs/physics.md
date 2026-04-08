# Physics & Collision System

## Vector Math (`core/Vector.scala`)

`Vector(x: Double, y: Double)` is an immutable case class supporting:

| Operation | Method |
|---|---|
| Addition | `v1 + v2` |
| Subtraction | `v1 - v2` |
| Scalar multiply | `v * d` |
| Scalar divide | `v / d` |
| Dot product | `v1.dotProduct(v2)` |
| Magnitude | `v.magnitude` → `sqrt(x²+y²)` |
| Unit vector | `v.unit` → `v / magnitude` |

`Vector.unitNormalAndTangent(v1, v2)` computes the unit normal (from v1 toward v2) and unit tangent (perpendicular) used in collision resolution.

## Ball Physics (`core/Ball.scala`)

### State

```scala
class Ball(
  val radius: Int,
  val colour: RGB,
  var position: Vector,   // mutable: updated each frame
  var velocity: Vector,   // mutable: updated each frame
  maxXy: Vector           // canvas bounds
)
```

`mass` is computed lazily as sphere-proportional: `(4/3) * π * r²` (note: uses r² not r³, a simplification).

### Movement (`ball.move(acceleration)(maxXy)`)

Each frame:
1. Scale velocity by `acceleration` (e.g. 0.98) — simulates friction
2. Add velocity to position
3. **Boundary reflection**: if position goes outside `[0, maxXy]`, clamp and negate that velocity component

```scala
// Pseudocode
velocity = velocity * acceleration
position = position + velocity
if (position.x < 0 || position.x > maxXy.x) velocity = velocity.copy(x = -velocity.x)
if (position.y < 0 || position.y > maxXy.y) velocity = velocity.copy(y = -velocity.y)
position = position.clamp(0, maxXy)
```

`Ball.maxSpeed = 100` — velocity is clamped per component before movement.

### Collision Detection (`ball.touching(other)`)

Two balls are touching if the distance between centers is less than the sum of their radii:

```scala
def touching(other: Ball): Boolean =
  (position - other.position).magnitude < (radius + other.radius)
```

### Collision Response (`Ball.collideIfNecessary(b1, b2)(cor)`)

Uses the **normal/tangent decomposition** method for 2D elastic collisions:

1. Compute unit normal `n` (from b1 center to b2 center) and unit tangent `t` (perpendicular)
2. Project both velocities onto normal and tangent:
   - `v1n = b1.velocity · n`, `v1t = b1.velocity · t`
   - `v2n = b2.velocity · n`, `v2t = b2.velocity · t`
3. Tangent components are **unchanged** by collision
4. Apply 1D elastic collision formula to normal components using masses `m1`, `m2` and coefficient of restitution `cor`:
   - `new_v1n = (m1*v1n + m2*v2n - m2*cor*(v1n - v2n)) / (m1 + m2)`
   - `new_v2n = (m1*v1n + m2*v2n + m1*cor*(v1n - v2n)) / (m1 + m2)`
5. Reconstruct velocity vectors: `new_v1 = new_v1n * n + v1t * t`

This gives physically correct 2D elastic/inelastic collision response.

### Per-Frame Collision Handling (`Game.handleCollisions()`)

Iterates all pairs of balls and calls `Ball.collideIfNecessary` for each pair. With N balls this is O(N²) per frame, acceptable for small N (≤8 in ProtectTheKing).

## Goal Zone Detection (`core/RectangleDelta.scala`)

```scala
case class RectangleDelta(x: Int, y: Int, deltaX: Int, deltaY: Int) {
  def contains(point: Vector): Boolean
}
```

`ProtectTheKing.run()` checks each ball's position against the opposing team's goal zone each frame. Balls inside the goal are removed from the `balls` sequence (scored).

Goal zones are thin vertical rectangles at the left and right canvas edges:
- Blue goal (left): `x=0`, width=`goalWidth`, centered vertically
- Green goal (right): `x=canvas.width - goalWidth`, same height

`halfGoalHeight = canvas.height / 10`, `goalWidth = canvas.width / 50`.
