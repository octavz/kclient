val Http4sVersion = "0.18.19"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "io.github.octavz",
    name := "kclient",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.7",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.specs2"     %% "specs2-core"          % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "io.circe" %% "circe-generic" % "0.9.3",
      "io.circe" %% "circe-literal" % "0.9.3",
      "com.github.mpilquist" %% "simulacrum" % "0.14.0",
      "io.monix" %% "monix-kafka-10" % "1.0.0-RC1"
    ),
    addCompilerPlugin("org.spire-math" %% "kind-projector"     % "0.9.8"),
    addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.2.4"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

  )

