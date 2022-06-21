/*
 * Copyright 2014 http4s.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.http4s.scalaxml

import scala.xml._
import scala.xml.transform._

trait ScalaXmlSuiteVersion {
  object stripComments extends RewriteRule {
    override def transform(n: Node): Seq[Node] =
      n match {
        case _: Comment => Seq.empty
        case n => Seq(n)
      }
  }

  object trimProper extends RewriteRule {
    override def transform(n: Node): Seq[Node] =
      Utility.trimProper(n)
  }

  // https://github.com/http4s/http4s-scala-xml/issues/32
  object normalize extends RuleTransformer(stripComments, trimProper)
}
