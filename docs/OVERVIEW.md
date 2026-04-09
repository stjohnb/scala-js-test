# Kwyjibo - Project Overview

## Purpose

A browser-based turn-based physics game called "Protect the King", built as a Scala.js playground. Two teams of balls compete to push the opposing team's balls into their goal zone. The project demonstrates Scala.js cross-compilation, 2D physics simulation, and HTML5 canvas rendering.

## Architecture

The project is split into three SBT subprojects:

```
build.sbt
├── core/          # Cross-compiled shared logic (JVM + JS)
├── web/           # Scala.js browser frontend
└── api/           # Play Framework backend (currently a stub)
```

**core** compiles to both JVM and JavaScript via `crossProject`. It contains all physics and domain types with no platform-specific dependencies.

**web** depends on `coreJS` and uses `scalajs-dom` for canvas and DOM access. It exports Scala objects to JavaScript via `@JSExport`.

**api** depends on `coreJVM` and uses Play Framework. Currently empty/unused.

### Source Layout

```
core/src/main/scala/net/bstjohn/kwyjibo/core/
├── Vector.scala          # 2D vector math (physics)
├── Ball.scala            # Physics object with collision logic
├── RGB.scala             # Color with toString -> CSS rgb()
└── RectangleDelta.scala  # Rectangle for boundary/goal zones

web/src/main/
├── scala/net/bstjohn/kwyjibo/web/game/
│   ├── Game.scala            # Abstract base: game loop, turn mechanics
│   └── ProtectTheKing.scala  # Concrete game: two-team ball game
└── resources/
    └── protectTheKing.html   # HTML entry point
```

## Key Patterns

### Physics Simulation

`Ball` carries mutable `position: Vector` and `velocity: Vector`. Each turn runs for `turnLength = 300` frames:

- `ball.move(acceleration)(maxXy)` — scales velocity by `acceleration` (friction), then clamps to bounds. Boundary hits reflect velocity.
- `Ball.collideIfNecessary(b1, b2)(coefficientOfRestitution)` — detects overlap via `touching`, decomposes velocities into normal/tangent components, applies conservation of momentum with restitution, updates both balls.

All other types (`Vector`, `RGB`, `RectangleDelta`) are immutable case classes.

### Game Loop (Template Method)

`Game` is an abstract trait. `ProtectTheKing` extends it and overrides:
- `initialBalls` — defines starting positions
- `handleKeyStrokes()` — registers DOM event handlers
- `draw()` — renders the canvas frame
- `run()` — called each frame; parent impl drives physics, subclass adds goal scoring

Turn flow:
1. User drags a ball to set its velocity (mouse down/up)
2. User presses Space or clicks "Go!" → `readyTurn()` exported to JS
3. `setInterval` runs `run()` every `timeStep = 2ms` for `turnLength = 300` frames
4. Balls with any velocity in a goal `RectangleDelta` are removed (scored)
5. Turn ends; player picks next ball

### JavaScript Interop

`@JSExport` on `ProtectTheKing` (an `object`) and key methods makes them callable from HTML:

```html
net.bstjohn.kwyjibo.web.game.ProtectTheKing().main()
net.bstjohn.kwyjibo.web.game.ProtectTheKing().readyTurn()
net.bstjohn.kwyjibo.web.game.ProtectTheKing().resizeCanvas(canvas)
```

The compiled output is `scala-js-tutorial-fastopt.js` (FastOpt/development mode).

### Game Rules (ProtectTheKing)

- Two teams: 4 blue balls (1 king r=10, 3 pawns r=30) and 4 green balls (same)
- Kings start at `1/6` and `5/6` of canvas width; pawns cluster around them
- `coefficientOfRestitution = 1.0` (fully elastic), `acceleration = 0.98`
- Goal zones: thin rectangles at the left (blue) and right (green) canvas edges
- Removing all of a team's balls (including king) wins the game (no explicit win condition check yet)

## Configuration

| Constant | Location | Value | Meaning |
|---|---|---|---|
| `timeStep` | `Game` | `2.0` | ms per game loop tick |
| `turnLength` | `Game` | `300` | frames per turn |
| `acceleration` | `Game` / `ProtectTheKing` | `0.99` / `0.98` | velocity decay per frame |
| `coefficientOfRestitution` | `Game` / `ProtectTheKing` | `0.8` / `1.0` | collision energy retention |
| `Ball.maxSpeed` | `Ball` | `100` | velocity clamping |
| `kingR` / `pawnR` | `ProtectTheKing` | `10` / `30` | ball radii |
| Scala version | `build.sbt` | `2.11.6` | |
| scalajs-dom | `build.sbt` | `0.8.0` | |
| sbt-scalajs | `project/plugins.sbt` | `0.6.2` | |

## Build

```sh
# Compile web module to JavaScript
sbt web/fastOptJS

# Output: web/target/scala-2.11/scala-js-tutorial-fastopt.js
# Open: web/src/main/resources/protectTheKing.html
```

The API module is aggregated by `root` but currently contains no sources.

## Detailed Docs

- [Physics & Collision](physics.md) — Vector math, collision algorithm, boundary reflection
