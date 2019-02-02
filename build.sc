import coursier.maven.MavenRepository
import mill._, scalalib._, scalafmt._, define._, util._

val Http4sVersion = "0.20.0-M4"
val LogbackVersion = "1.2.3"
val Log4catsVersion = "0.2.0"
val CirceVersion = "0.11.0"
val KafkaVersion = "2.1.0"
val RefinedVersion = "0.9.3"

object backend extends ScalaModule with ScalafmtModule {
  override def forkArgs = Seq("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5555")

  override def repositories = super.repositories ++ Seq(
    MavenRepository("https://oss.sonatype.org/content/repositories/releases")
  )

  def scalaVersion = "2.12.8"

  override def scalacOptions = Seq(
    "-encoding",
    "utf8", // Option and arguments on same line
    //    "-Xfatal-warnings",
    "-Ypartial-unification",
    "-deprecation",
    "-unchecked",
    "-feature",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials",
    "-language:postfixOps",
    //    "-Xlog-implicits",
    "-Xplugin:clippy-plugin_2.12-0.5.3-bundle.jar"
  )

  def runDebug(port: Int, args: String*) = T.command {
    import mill.modules.Jvm
    import mill.eval.Result
    try Result.Success(Jvm.runSubprocess(
      finalMainClass(),
      runClasspath().map(_.path),
      Seq(s"-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=$port"),
      forkEnv(),
      args,
      workingDir = forkWorkingDir()
    )) catch {
      case e: Exception =>
        Result.Failure("subprocess failed")
    }
  }

  override def compileIvyDeps: Target[Loose.Agg[Dep]] = Agg(
    ivy"com.lihaoyi::mill-scalalib:0.3.5"
  )

  override def scalacPluginIvyDeps = Agg(
    ivy"org.spire-math::kind-projector:0.9.9",
    ivy"com.olegpy::better-monadic-for:0.2.4",
    ivy"org.scalamacros:::paradise:2.1.1",
    ivy"org.json4s::json4s-native:3.6.3",
    ivy"com.softwaremill.clippy::plugin:0.5.3"
  )

  override def ivyDeps = Agg(
    ivy"org.http4s::http4s-blaze-server:$Http4sVersion",
    ivy"org.http4s::http4s-circe:$Http4sVersion",
    ivy"org.http4s::http4s-dsl:$Http4sVersion",
    ivy"io.circe::circe-generic:$CirceVersion",
    ivy"io.circe::circe-literal:$CirceVersion",
    ivy"com.github.mpilquist::simulacrum:0.14.0",
    ivy"io.monix::monix-kafka-10:1.0.0-RC1",
    ivy"org.typelevel::cats-mtl-core:0.4.0",
    ivy"io.chrisdavenport::log4cats-extras:$Log4catsVersion",
    ivy"io.chrisdavenport::log4cats-scribe:$Log4catsVersion",
    ivy"com.outr::scribe:2.7.1",
    ivy"com.github.pureconfig::pureconfig:0.10.1",
    ivy"org.scala-lang.modules::scala-java8-compat:0.9.0",
    ivy"com.olegpy::meow-mtl:0.2.0",
    ivy"org.apache.kafka:kafka-clients:$KafkaVersion",
    ivy"org.apache.kafka::kafka:$KafkaVersion",
    ivy"eu.timepit::refined:$RefinedVersion",
    ivy"eu.timepit::refined-cats:$RefinedVersion",
    ivy"eu.timepit::refined-pureconfig:$RefinedVersion"
  )

  object test extends Tests {
    override def ivyDeps = Agg(
      ivy"org.scalatest::scalatest::3.0.5",

    )

    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }

  object it extends Tests {
    override def ivyDeps = Agg(
      ivy"org.scalatest::scalatest::3.0.5",
      ivy"com.dimafeng::testcontainers-scala:0.22.0"
    )

    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }

}
