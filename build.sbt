ThisBuild / tlBaseVersion := "0.23"
ThisBuild / developers := List(
  tlGitHubDev("rossabaker", "Ross A. Baker")
)
ThisBuild / startYear := Some(2014)

val Scala212 = "2.12.20"
val Scala213 = "2.13.16"
ThisBuild / crossScalaVersions := Seq(Scala212, Scala213, "3.3.5")
ThisBuild / scalaVersion := Scala213

lazy val root = project.in(file(".")).aggregate(scalaXml2, scalaXml1).enablePlugins(NoPublishPlugin)

val http4sVersion = "0.23.30"
val scalacheckXmlVersion = "0.1.0"
val scalaXml1Version = "1.3.1"
val scalaXml2Version = "2.3.0"
val munitVersion = "1.1.0"
val munitCatsEffectVersion = "2.0.0"

lazy val scalaXml2 = project
  .in(file("scala-xml-2"))
  .settings(
    name := "http4s-scala-xml",
    description := "Provides scala-xml codecs for http4s",
    tlMimaPreviousVersions ++= (0 to 11).map(y => s"0.23.$y").toSet,
    libraryDependencies += "org.scala-lang.modules" %%% "scala-xml" % scalaXml2Version,
    commonSettings,
  )

lazy val scalaXml1 = project
  .in(file("scala-xml-1"))
  .settings(
    name := "http4s-scala-xml-1",
    description := "Provides scala-xml codecs for http4s",
    tlMimaPreviousVersions ++= Set("0.23.0"),
    crossScalaVersions := Seq(Scala212, Scala213),
    libraryDependencies += "org.scala-lang.modules" %%% "scala-xml" % scalaXml1Version,
    dependencyOverrides += "org.scala-lang.modules" %%% "scala-xml" % scalaXml1Version,
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
