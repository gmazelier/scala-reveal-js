
ThisBuild / scalaVersion := "3.2.2"

val reactVersion = "18.2.0"

lazy val common = Seq(
  version      := "-",
  libraryDependencies ++= Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % "2.1.1",
    "org.scala-js" %%% "scalajs-dom" % "2.4.0"
  ),
  Compile / npmDependencies ++= Seq(
    "react" -> reactVersion,
    "react-dom" -> reactVersion),
  scalaJSUseMainModuleInitializer := true
)

lazy val root = project
  .in(file("."))
  .aggregate(
    myTalk,
    shared
  )

lazy val shared = project
  .in(file("shared"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(common)

lazy val myTalk = project
  .in(file("my-talk"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(common)
  .dependsOn(shared)
