ThisBuild / tlBaseVersion := "1.0"
ThisBuild / developers := List(
  tlGitHubDev("rossabaker", "Ross A. Baker")
)
ThisBuild / startYear := Some(2014)

val Scala213 = "2.13.12"
ThisBuild / crossScalaVersions := Seq(Scala213, "3.3.3")
ThisBuild / scalaVersion := Scala213

lazy val root = project.in(file(".")).aggregate(scalaXml2).enablePlugins(NoPublishPlugin)

val http4sVersion = "1.0.0-M42"
val scalacheckXmlVersion = "0.1.0"
val scalaXmlVersion = "2.3.0"
val munitVersion = "1.0.0"
val munitCatsEffectVersion = "2.0.0-RC1"

lazy val scalaXml2 = project
  .in(file("scala-xml-2"))
  .settings(
    name := "http4s-scala-xml",
    description := "Provides scala-xml codecs for http4s",
    tlMimaPreviousVersions ++= (0 to 11).map(y => s"0.23.$y").toSet,
    libraryDependencies += "org.scala-lang.modules" %%% "scala-xml" % scalaXmlVersion,
    commonSettings,
  )

lazy val commonSettings = Seq(
  Compile / unmanagedSourceDirectories += (LocalRootProject / baseDirectory).value / "scala-xml" / "src" / "main" / "scala",
  Test / unmanagedSourceDirectories += (LocalRootProject / baseDirectory).value / "scala-xml" / "src" / "test" / "scala",
  libraryDependencies ++= Seq(
    "org.http4s" %%% "http4s-core" % http4sVersion,
    "org.http4s" %%% "http4s-laws" % http4sVersion % Test,
    "org.scalameta" %%% "munit-scalacheck" % munitVersion % Test,
    "org.typelevel" %%% "munit-cats-effect" % munitCatsEffectVersion % Test,
    "org.typelevel" %%% "scalacheck-xml" % scalacheckXmlVersion % Test,
  ),
)

lazy val docs = project
  .in(file("site"))
  .dependsOn(scalaXml2)
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s" %%% "http4s-dsl" % http4sVersion,
      "org.http4s" %%% "http4s-circe" % http4sVersion,
      "io.circe" %%% "circe-generic" % "0.14.1",
    )
  )
  .enablePlugins(Http4sOrgSitePlugin)
