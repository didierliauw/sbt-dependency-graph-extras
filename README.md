# sbt-dependency-graph-extras

Compact dependency tree visualization for your project's dependencies.

## Usage Instructions

sbt-dependency-graph-extras uses the sbt-dependency-graph plugin to add the compactDependencyTree visualization.
This visualization is similar to dependencyTree, but repeated dependencies (indicated with ```=>```) are only 
displayed once in their own dependency sub tree block. 
Optionally, it is possible to provide a dependency. If given, the tree will only contain
paths from the root to that dependency.

This visualization makes it easier to understand projects with a lot of repeated dependencies.
For example, the ```dependencyTree``` of ```twitter-server``` contains 30184 lines compared to ```compactDependencyTree```
with 368 lines. ```whatDependsOn```  is used to find out which artifacts depend on a particular library.
```whatDependsOn io.netty netty-codec``` contains 539 lines and ```compactDependencyTree io.netty netty-codec```
contains 50 lines. 

Put the following line in your `project/plugins.sbt` file.

```scala
addSbtPlugin("com.github.didierliauw" % "sbt-dependency-graph-extras" % "0.1.0-SNAPSHOT")
```

## Main task

The main task is ```compactDependencyTree [<organization> <module> <revision>?]```. 
The output is similar to the dependencyTree visualization, except instead of one big tree with repeated dependencies.
It will mark those repeated dependencies with ```=>```. Those dependencies will then displayed in a different block.
If an optional artifact is provided then only paths from the root to the provided dependencies are considered.

Example usage:
```
sbt:root> compactDependencyTree io.netty netty-codec
root:root_2.12:0.1.0-SNAPSHOT [S]
  +-com.twitter:twitter-server_2.12:21.11.0 [S]
    +-com.twitter:finagle-http_2.12:21.11.0 [S] =>
    +-com.twitter:finagle-stats-core_2.12:21.11.0 [S]
      +-com.twitter:finagle-http_2.12:21.11.0 [S] =>

com.twitter:finagle-http_2.12:21.11.0 [S]
  +-com.twitter:finagle-base-http_2.12:21.11.0 [S] =>
  +-com.twitter:finagle-http2_2.12:21.11.0 [S]
  | +-com.twitter:finagle-base-http_2.12:21.11.0 [S] =>
  | +-com.twitter:finagle-netty4-http_2.12:21.11.0 [S] =>
  | +-com.twitter:finagle-netty4_2.12:21.11.0 [S] =>
  | +-io.netty:netty-codec-http2:4.1.66.Final
  | | +-io.netty:netty-codec-http:4.1.66.Final =>
  | | +-io.netty:netty-codec:4.1.66.Final
  | | +-io.netty:netty-handler:4.1.66.Final =>
  | |
  | +-io.netty:netty-codec-http:4.1.66.Final =>
  | +-io.netty:netty-handler-proxy:4.1.66.Final =>
  | +-io.netty:netty-handler:4.1.66.Final =>
  |
  +-com.twitter:finagle-netty4-http_2.12:21.11.0 [S] =>

com.twitter:finagle-base-http_2.12:21.11.0 [S]
  +-io.netty:netty-codec-http:4.1.66.Final =>
  +-io.netty:netty-handler-proxy:4.1.66.Final =>
  +-io.netty:netty-handler:4.1.66.Final =>

com.twitter:finagle-netty4-http_2.12:21.11.0 [S]
  +-com.twitter:finagle-base-http_2.12:21.11.0 [S] =>
  +-com.twitter:finagle-netty4_2.12:21.11.0 [S] =>
  +-io.netty:netty-codec-http:4.1.66.Final =>

com.twitter:finagle-netty4_2.12:21.11.0 [S]
  +-io.netty:netty-handler-proxy:4.1.66.Final =>
  +-io.netty:netty-handler:4.1.66.Final =>

io.netty:netty-codec-http:4.1.66.Final
  +-io.netty:netty-codec:4.1.66.Final
  +-io.netty:netty-handler:4.1.66.Final =>

io.netty:netty-handler:4.1.66.Final
  +-io.netty:netty-codec:4.1.66.Final

io.netty:netty-handler-proxy:4.1.66.Final
  +-io.netty:netty-codec-http:4.1.66.Final =>
  +-io.netty:netty-codec-socks:4.1.66.Final
  | +-io.netty:netty-codec:4.1.66.Final
  |
  +-io.netty:netty-codec:4.1.66.Final
```