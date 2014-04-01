organization  := "com.example"

name          := "maven"

version       := "0.1"

scalaVersion  := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  Seq(
    "com.github.athieriot" %% "jexter" % "0.5.1",
    "org.eclipse.jetty"         %   "jetty-webapp"      % "8.1.13.v20130916"    % "container",
    "org.eclipse.jetty.orbit"   %   "javax.servlet"     % "3.0.0.v201112011016" % "container"  artifacts Artifact("javax.servlet", "jar", "jar")
  )
}

// Part of the xsbt-web-plugin configuration
seq(webSettings: _*)
