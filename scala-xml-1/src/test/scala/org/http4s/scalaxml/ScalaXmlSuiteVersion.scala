package org.http4s.scalaxml

import scala.xml._
import scala.xml.transform._

trait ScalaXmlSuiteVersion {
  // https://github.com/http4s/http4s-scala-xml/issues/32
  object stripComments extends RewriteRule {
    override def transform(n: Node): Seq[Node] =
      n match {
        case _: Comment => Seq.empty
        case n => Seq(n)
      }
  }

  object normalize extends RuleTransformer(stripComments)
}
