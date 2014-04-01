organization  := "com.github.athieriot"

version       := "0.5.1"

scalaVersion  := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.0"
  val sprayV = "1.3.1"
  Seq(
    "io.spray"                  %   "spray-servlet"       % sprayV,
    "io.spray"                  %   "spray-routing"       % sprayV,
    "io.spray"                  %   "spray-testkit"       % sprayV,
    "org.eclipse.jetty"         %   "jetty-webapp"        % "8.1.13.v20130916"    % "container",
    "org.eclipse.jetty.orbit"   %   "javax.servlet"       % "3.0.0.v201112011016" % "container"  artifacts Artifact("javax.servlet", "jar", "jar"),
    "com.typesafe.akka"         %%  "akka-actor"          % akkaV,
    "com.typesafe.akka"         %%  "akka-testkit"        % akkaV,
    "org.specs2"                %%  "specs2"              % "2.3.10" % "test",
    "org.fusesource.scalate"    %   "scalate-core_2.10"   % "1.6.1",
    "com.typesafe"              %   "config"              % "1.2.0",
    "org.slf4j"                 %   "slf4j-log4j12"       % "1.7.5",
    "com.typesafe"              %%  "scalalogging-slf4j"  % "1.1.0",
    "org.scala-lang"            %   "scala-compiler"      % scalaVersion.value
  )
}

seq(webSettings: _*)

packagedArtifacts <<= packagedArtifacts map { as => as.filter(_._1.`type` != "war") }

publishArtifact in (Compile, packageBin) := true

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := (
  <url>https://github.com/athieriot/jexter</url>
  <licenses>
    <license>
      <name>MIT</name>
      <url>http://opensource.org/licenses/MIT</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:athieriot/jexter.git</url>
    <connection>scm:git:git@github.com:athieriot/jexter.git</connection>
  </scm>
  <developers>
    <developer>
      <id>athieriot</id>
      <name>Aurélien Thieriot</name>
      <url>http://aurelien.thier.io</url>
    </developer>
  </developers>)
