val Http4sVersion = "0.20.0-M4"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"
val Log4catsVersion = "0.2.0"
val CirceVersion = "0.11.0"

scalacOptions ++= Seq(
  "-encoding",
  "utf8", // Option and arguments on same line
  "-Xfatal-warnings",
  "-Ypartial-unification",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    organization := "io.github.octavz",
    name := "kclient",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-literal" % CirceVersion,
      "com.github.mpilquist" %% "simulacrum" % "0.14.0",
      "io.monix" %% "monix-kafka-10" % "1.0.0-RC2",
      "org.typelevel" %% "cats-mtl-core" % "0.4.0",
      "org.apache.kafka" % "kafka-clients" % "2.1.0",
      "io.chrisdavenport" %% "log4cats-extras" % Log4catsVersion,
      "io.chrisdavenport" %% "log4cats-scribe" % Log4catsVersion,
      "com.outr" %% "scribe" % "2.7.1",
      "com.olegpy" %% "meow-mtl" % "0.2.0",
      "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0",
      "com.github.pureconfig" %% "pureconfig" % "0.10.1",
      "org.scalatest" %% "scalatest" % "3.0.5" % "it,test",
      "com.dimafeng" %% "testcontainers-scala" % "0.22.0" % "it"
    ),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4"),
    addCompilerPlugin(("org.scalamacros" % "paradise" % "2.1.1").cross(CrossVersion.full))
  )
