# http4s-scala-xml

Provides http4s entity codec instances for [scala-xml].

## `http4s-scala-xml`

This is probably the artifact you want.  It works with scala-xml-2.x.

### SBT coordinates

```scala
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-scala-xml" % http4sScalaXmlV
)
```

## `http4s-scala-xml-1`

This repository also publishes an alternate `http4s-scala-xml-1`
artifact.  The Scala package is the same, so this dependency must
never be bundled with `http4s-scala-xml`.  It exists because several
signficant libraries, like [Twirl], are still based on scala-xml-1.x
in Scala 2.  Use this library to avoid diamond dependencies, but
upgrade when you can.

### SBT coordinates

```scala
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-scala-xml-1" % http4sScalaXmlV
)
```


## Compatibility

| artifact           | version | http4s-core | scala-xml | Scala 2.12 | Scala 2.13 | Scala 3 | Status |   |
|:-------------------|:--------|:------------|:----------|------------|------------|---------|--------|---|
| http4s-scala-xml   | 0.23.x  | 0.23.x      | 2.x       | ✅         | ✅         | ✅      | Stable |   |
| http4s-scala-xml-1 | 0.23.x  | 0.23.x      | 1.x       | ✅         | ✅         | ❌      | Stable |   |

[scala-xml]: https://github.com/scala/scala-xml
[twirl]: https://github.com/playframework/twirl
