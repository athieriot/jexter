organization  := "com.github.athieriot"

version       := "0.1"

scalaVersion  := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= {
  val akkaV = "2.2.3"
  val sprayV = "1.2-RC1"
  Seq(
    "io.spray"                  %   "spray-servlet"     % sprayV,
    "io.spray"                  %   "spray-routing"     % sprayV,
    "io.spray"                  %   "spray-testkit"     % sprayV,
    "org.eclipse.jetty"         %   "jetty-webapp"      % "8.1.13.v20130916"    % "container",
    "org.eclipse.jetty.orbit"   %   "javax.servlet"     % "3.0.0.v201112011016" % "container"  artifacts Artifact("javax.servlet", "jar", "jar"),
    "com.typesafe.akka"         %%  "akka-actor"        % akkaV,
    "com.typesafe.akka"         %%  "akka-testkit"      % akkaV,
    "org.specs2"                %%  "specs2"            % "2.2.3" % "test",
    "org.fusesource.scalate"    %   "scalate-core_2.10" % "1.6.1",
    "com.typesafe"              %   "config"            % "1.0.2"
  )
}

seq(webSettings: _*)

packagedArtifacts <<= packagedArtifacts map { as => as.filter(_._1.`type` != "war") }

publishArtifact in (Compile, packageBin) := true

publishMavenStyle := true

publishArtifact in Test := false

publishTo := Some(Resolver.file("file",  new File( "./mvn-repo" )) )