package org.http4s.scalaxml

import scala.xml.transform._

trait ScalaXmlSuiteVersion {
  // https://github.com/http4s/http4s-scala-xml/issues/32
  //
  // Nothing to do here but make the tests compatible with
  // scala-xml-1's parser.
  object normalize extends RuleTransformer()
}
