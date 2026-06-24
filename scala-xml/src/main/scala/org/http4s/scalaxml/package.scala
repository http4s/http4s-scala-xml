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

package org.http4s

import javax.xml.XMLConstants
import javax.xml.parsers.SAXParserFactory

package object scalaxml extends ElemInstances {
  override val saxFactory = {
    val parserFactory = SAXParserFactory.newInstance
    parserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
    parserFactory.setFeature(
      "http://apache.org/xml/features/nonvalidating/load-external-dtd",
      false,
    )
    parserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    parserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    parserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false)
    parserFactory.setFeature("http://xml.org/sax/features/resolve-dtd-uris", false)
    parserFactory.setXIncludeAware(false)
    parserFactory.setNamespaceAware(false)
    parserFactory
  }
}
