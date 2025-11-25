ThisBuild / tlBaseVersion := "0.24"
ThisBuild / developers := List(
  tlGitHubDev("rossabaker", "Ross A. Baker")
)
ThisBuild / startYear := Some(2014)

val Scala212 = "2.12.20"
val Scala213 = "2.13.16"
ThisBuild / crossScalaVersions := Seq(Scala212, Scala213, "3.3.7")
ThisBuild / scalaVersion := Scala213

lazy val root = project.in(file(".")).aggregate(scalaXml).enablePlugins(NoPublishPlugin)

val http4sVersion = "0.23.33"
val scalacheckXmlVersion = "0.1.1"
val scalaXml2Version = "2.4.0"
val munitVersion = "1.2.0"
val munitCatsEffectVersion = "2.1.0"

lazy val scalaXml = project
  .in(file("scala-xml"))
  .settings(
    name := "http4s-scala-xml",
    description := "Provides scala-xml codecs for http4s",
    libraryDependencies ++= Seq(
      "org.http4s" %%% "http4s-core" % http4sVersion,
      "org.http4s" %%% "http4s-laws" % http4sVersion % Test,
      "org.scala-lang.modules" %%% "scala-xml" % scalaXml2Version,
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
