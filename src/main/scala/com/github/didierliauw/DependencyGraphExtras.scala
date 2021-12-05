package com.github.didierliauw

import net.virtualvoid.sbt.graph.DependencyGraphSettings.ArtifactPattern
import net.virtualvoid.sbt.graph.{Edge, Module, ModuleGraph, ModuleId}
import sbt.internal.util.complete.DefaultParsers.Space
import sbt.internal.util.complete.Parser
import sbt.internal.util.complete.DefaultParsers._

object DependencyGraphExtras {
  implicit class RichModuleGraph(val m: ModuleGraph) {
    def ++(other: ModuleGraph) = ModuleGraph(
      other.nodes.foldLeft(m.nodes){ case (l,m) => if(l.contains(m)) l else l :+ m},
      other.edges.foldLeft(m.edges){ case (l,e) => if(l.contains(e)) l else l :+ e})
  }
  val artifactParser: Parser[Option[ArtifactPattern]] = ((Space ~> NotSpace <~ Space) ~ NotSpace ~ (Space ~> NotSpace).?).?.map(_.map {
    case ((org, name), maybeVersion) => ArtifactPattern(org,name,maybeVersion)
  })
  def findModules(m: ModuleGraph, organisation: String, name: String, version: Option[String]): Seq[Module] =
    m.nodes.collect{ case n if n.id.organisation == organisation && n.id.name == name && version.forall(v => n.id.version == v) => n }

  def makeAncestorMap(dMap: Map[ModuleId, Seq[Module]])(module: ModuleId, current: Map[ModuleId, Set[ModuleId]]): Map[ModuleId, Set[ModuleId]] = {
    if(current.contains(module)) current else if (!dMap.contains(module)) current + (module -> Set()) else {
      dMap(module).foldLeft(current) {
        case (c,m) =>
          val newMap = makeAncestorMap(dMap)(m.id, c)
          newMap + (module -> (newMap.getOrElse(module, Set()) + m.id ++ newMap(m.id)))
      }
    }
  }

  def filterPathsTo(graph: ModuleGraph, targetId: ModuleId): Option[ModuleGraph] = {
    val target = graph.module(targetId)
    val reverse = graph.reverseDependencyMap
    val ancestors = makeAncestorMap(reverse)(targetId, Map())
    val roots = graph.roots.map(_.id)

    def isOnPath(m: ModuleId): Boolean = roots.exists(r => r == m || ancestors.get(m).exists(_.contains(r)))
    def helper(original: ModuleId)(g: ModuleGraph): ModuleGraph = {
      reverse.get(original).map(_.foldLeft(g){
        case (c,m) if isOnPath(m.id) => helper(m.id)(c ++ ModuleGraph(Seq(m), Seq(Edge(m.id, original))))
        case (c,_) => c
      }).getOrElse(g)
    }
    Some(helper(targetId)(ModuleGraph(Seq(target), Seq())))
  }

  def filterPathsToMultiple(graph: ModuleGraph, targets: Seq[ModuleId]): Option[ModuleGraph] = {
    val t = targets.map(target => filterPathsTo(graph, target)).flatten
    if(t.isEmpty) None else Some(t.foldLeft(t.head) { case (x,y) => x ++ y})
  }

  def makeCompact(graph: ModuleGraph): Seq[ModuleGraph] = {
    val dep = graph.dependencyMap
    val rev = graph.reverseDependencyMap
    val root = graph.roots.head
    def helper(todo: Seq[ModuleId], result: Seq[ModuleGraph]): Seq[ModuleGraph] = {
      val ad = result.map(_.roots.head.id).toSet
      def makeGraph(currentModule: ModuleId, currentGraph: ModuleGraph, todo: Seq[ModuleId]): (ModuleGraph, Seq[ModuleId]) = {
        dep(currentModule).foldLeft((currentGraph, todo)) {
          case ((g,td),m) if ad(m.id) || td.contains(m.id) => (g ++ ModuleGraph(Seq(m), Seq(currentModule -> m.id)), td)
          case ((g,td),m) if rev(m.id).size > 1 && dep(m.id).size > 0 => (g ++ ModuleGraph(Seq(m), Seq(currentModule -> m.id)), td :+ m.id)
          case ((g,td),m) => makeGraph(m.id, g ++ ModuleGraph(Seq(m), Seq(currentModule -> m.id)), td)
        }
      }
      todo match {
        case Nil => result
        case head +: tail =>
          val (g,t) = makeGraph(head, ModuleGraph(Seq(graph.module(head)), Nil), tail)
          helper(t, result :+ g)
      }
    }
    val result = helper(Seq(root.id), Nil)
    val ownGraphs = result.map(_.roots.map(_.id)).flatten.toSet
    result.map(g => g.copy(nodes = g.nodes.map(n => if(ownGraphs(n.id) && !g.roots.contains(n)) n.copy(extraInfo = n.extraInfo + " =>") else n)))
  }
}
