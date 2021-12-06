package io.github.didierliauw.dependencygraphextras

import net.virtualvoid.sbt.graph.DependencyGraphKeys.moduleGraph
import net.virtualvoid.sbt.graph.DependencyGraphSettings.ArtifactPattern
import net.virtualvoid.sbt.graph.ModuleGraph
import net.virtualvoid.sbt.graph.rendering.AsciiTree
import sbt._
import sbt.Keys._
import DependencyGraphExtras._

object DependencyGraphExtrasPlugin extends AutoPlugin {
  val compactDependencyTree =
    InputKey[Unit]("compactDependencyTree",
      "Shows a compact dependency tree where duplicated sub tree are shown in separate blocks and " +
        "if given dependency identifier show only the paths to that dependency")
  override def trigger = allRequirements
  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(Compile, Test, IntegrationTest, Runtime, Provided, Optional).map { config =>
      config / compactDependencyTree := {
        val g = (Compile / moduleGraph).value
        val graph = artifactParser.parsed.fold(Option(g)) {
          case ArtifactPattern(org, name, version) =>
            val targetModules = findModules(g, org, name, version).map(_.id)
            filterPathsToMultiple(g, targetModules)
        }
        def logCompactGraphs(m: Seq[ModuleGraph]) = streams.value.log.info(m.map(AsciiTree.asciiTree).mkString("\n"))
        graph.map(makeCompact)
          .fold(streams.value.log.error("Project does not contain target artifact as a dependency"))(logCompactGraphs)
      }
    }
}
