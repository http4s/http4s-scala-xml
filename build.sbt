ThisBuild / tlBaseVersion := "1.0"
ThisBuild / developers := List(
  tlGitHubDev("rossabaker", "Ross A. Baker")
)
ThisBuild / startYear := Some(2014)

val Scala213 = "2.13.18"
ThisBuild / crossScalaVersions := Seq(Scala213, "3.3.8")
ThisBuild / scalaVersion := Scala213

lazy val root = project.in(file(".")).aggregate(scalaXml).enablePlugins(NoPublishPlugin)

val http4sVersion = "1.0.0-M46"
val scalacheckXmlVersion = "0.1.1"
val scalaXmlVersion = "2.4.0"
val munitVersion = "1.3.0"
val munitCatsEffectVersion = "2.2.0"

lazy val scalaXml = project
  .in(file("scala-xml"))
  .settings(
    name := "http4s-scala-xml",
    description := "Provides scala-xml codecs for http4s",
    libraryDependencies ++= Seq(
      "org.http4s" %%% "http4s-core" % http4sVersion,
      "org.http4s" %%% "http4s-laws" % http4sVersion % Test,
      "org.scala-lang.modules" %%% "scala-xml" % scalaXmlVersion,
      "org.scalameta" %%% "munit-scalacheck" % munitVersion % Test,
      "org.typelevel" %%% "munit-cats-effect" % munitCatsEffectVersion % Test,
      "org.typelevel" %%% "scalacheck-xml" % scalacheckXmlVersion % Test,
    ),
  )

lazy val docs = project
  .in(file("site"))
  .dependsOn(scalaXml)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s" %%% "http4s-dsl" % http4sVersion,
      "org.http4s" %%% "http4s-circe" % http4sVersion,
      "io.circe" %%% "circe-generic" % "0.14.1",
    )
  )
  .enablePlugins(Http4sOrgSitePlugin)
