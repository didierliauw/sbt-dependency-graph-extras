ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.didierliauw"
ThisBuild / homepage := Some(url("https://github.com/didierliauw/sbt-dependency-graph-extras"))
ThisBuild / scalaVersion := "2.12.14"

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-dependency-graph-extras",
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
  )