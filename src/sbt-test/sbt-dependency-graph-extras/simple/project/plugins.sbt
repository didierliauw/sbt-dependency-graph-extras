sys.props.get("plugin.version") match {
  case Some(x) => addSbtPlugin("io.github.didierliauw" % "sbt-dependency-graph-extras" % x)
  case _ => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}