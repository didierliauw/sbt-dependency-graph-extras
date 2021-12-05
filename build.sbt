ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.github.didierliauw"

ThisBuild / homepage := Some(url("https://github.com/didierliauw/sbt-dependency-graph-extras"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/didierliauw/sbt-dependency-graph-extras"),
    "scm:git:https://github.com/didierliauw/sbt-dependency-graph-extras.git"
  )
)
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true

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