
ThisBuild / scalaVersion := "3.2.2"

val reactVersion = "18.2.0"

val copyTarget = SettingKey[File](label = "copyTarget", description = "scala.js linker artifact copy target directory")
val copyJS     = TaskKey[Unit](label = "copyJS", description = "Copy scala.js linker artifacts to another location after linking.")

lazy val common = Seq(
  version      := "-",
  copyTarget := baseDirectory.value / "js",
  libraryDependencies ++= Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % "2.1.1",
    "org.scala-js" %%% "scalajs-dom" % "2.4.0"
  ),
  webpackBundlingMode := BundlingMode.LibraryOnly(),
  Compile / npmDependencies ++= Seq(
    "react" -> reactVersion,
    "react-dom" -> reactVersion),
  // Credits to: https://github.com/aappddeevv/sbt-copy-scalajs-artifact
  copyJS := {
    val logger = streams.value.log
    val outputDir: File = copyTarget.value
    val src: File = (Compile / scalaJSLinkedFile).value.data

    val build = Seq(src) ++ Seq("library", "loader").map(s => file(src.getCanonicalPath.replace(".js", s"-$s.js")))
    val mapFiles = build.map(f => file(f.getCanonicalPath + ".map")).filter(_.exists)
    val buildWithMap = build ++ mapFiles
    logger.info(s"Copying artifacts $buildWithMap to [$outputDir]")
    IO.copy(
      buildWithMap.map(f => (f, outputDir / f.name)),
      CopyOptions(overwrite = true, preserveLastModified = true, preserveExecutable = true)
    )
  },
  webpack / copyJS := (copyJS triggeredBy Compile / fastOptJS / webpack).value,
  fullOptJS / copyJS := (copyJS triggeredBy Compile / fullOptJS).value,
  scalaJSUseMainModuleInitializer := true
)

lazy val root = project
  .in(file("."))
  .aggregate(myTalk, shared)

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
