val kwyjibo = "kwyjibo"
val scalaV = "2.11.6"

version in ThisBuild := "git describe --tags --always --dirty".!!.trim.replaceFirst("^v", "")

organization in ThisBuild := "net.bstjohn"

lazy val core = (crossProject.crossType(CrossType.Pure) in file("core")).settings(
    name := kwyjibo + "-core",
    scalaVersion := scalaV
  ).
  jvmSettings(
    // Add JVM-specific settings here
  ).
  jsSettings(
    // Add JS-specific settings here
  )

lazy val web = (project in file("web")).enablePlugins(ScalaJSPlugin)
  .aggregate(coreJS)
  .dependsOn(coreJS)
  .settings(
    name := kwyjibo + "-web",
    scalaVersion := scalaV,
    scalaJSStage in Global := FastOptStage,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0",
    publish := {},
    publishLocal := {}
  )

lazy val api = (project in file("api"))
  .enablePlugins(PlayScala)
  .settings(
    name := kwyjibo + "-api",
    scalaVersion := scalaV
  )
  .aggregate(coreJVM)

lazy val coreJVM = core.jvm
lazy val coreJS = core.js