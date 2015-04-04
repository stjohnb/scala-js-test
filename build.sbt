lazy val core = project in file("core")

lazy val web = (project in file("web")).enablePlugins(ScalaJSPlugin) settings(
  name := "Scala.js Tutorial",
  scalaVersion := "2.11.6",
  scalaJSStage in Global := FastOptStage,
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0"
  )

lazy val api = (project in file("api"))
  .enablePlugins(PlayScala)
  .aggregate(core)
  .dependsOn(core)
