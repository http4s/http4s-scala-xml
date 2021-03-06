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
package scalaxml

import cats.effect._
import cats.syntax.all._
import fs2.Chunk
import fs2.Stream
import fs2.text.decodeWithCharset
import fs2.text.utf8
import munit.CatsEffectSuite
import munit.ScalaCheckEffectSuite
import org.http4s.Status.Ok
import org.http4s.headers.`Content-Type`
import org.http4s.laws.discipline.arbitrary._
import org.scalacheck.Prop._
import org.scalacheck.effect.PropF._
import org.typelevel.ci._
import org.typelevel.scalacheck.xml.generators._

import java.nio.charset.StandardCharsets
import scala.xml.Elem

class ScalaXmlSuite extends CatsEffectSuite with ScalaCheckEffectSuite with ScalaXmlSuiteVersion {
  def getBody(body: EntityBody[IO]): IO[String] =
    body.through(utf8.decode).foldMonoid.compile.lastOrError

  def strEntity(body: String): Entity[IO] = Entity(Stream(body).through(utf8.encode))

  def writeToString[A](a: A)(implicit W: EntityEncoder[IO, A]): IO[String] =
    Stream
      .emit(W.toEntity(a))
      .flatMap(_.body)
      .through(utf8.decode)
      .foldMonoid
      .compile
      .last
      .map(_.getOrElse(""))

  val server: Request[IO] => IO[Response[IO]] = { req =>
    req.decode { (elem: Elem) =>
      IO.pure(Response[IO](Ok).withEntity(elem.label))
    }
  }

  test("round trips utf-8") {
    forAllF(genXml) { (elem: Elem) =>
      val normalized = normalize(elem).asInstanceOf[Elem]
      Request[IO]()
        .withEntity(normalized)
        .as[Elem]
        .assertEquals(normalized)
    }
  }

  test("parse XML in parallel") {
    val req = Request(entity =
      strEntity(
        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><html><h1>h1</h1></html>"""
      )
    )
    // https://github.com/http4s/http4s/issues/1209

    (0 to 5).toList
      .parTraverse(_ => server(req).flatMap(r => getBody(r.body)))
      .map { bodies =>
        bodies.foreach { body =>
          assertEquals(body, "html")
        }
      }
  }

  test("return 400 on parse error") {
    val entity = strEntity("This is not XML.")
    val tresp = server(Request[IO](entity = entity))
    tresp.map(_.status).assertEquals(Status.BadRequest)
  }

  test("htmlEncoder renders HTML") {
    val html = <html><body>Hello</body></html>
    implicit val cs: Charset = Charset.`UTF-8`
    assertIO(
      writeToString(html),
      """<?xml version='1.0' encoding='UTF-8'?>
        |<html><body>Hello</body></html>""".stripMargin,
    )
  }

  test("encode to UTF-8") {
    val hello = <hello name="G??nther"/>
    assertEquals(
      xmlEncoder(Charset.`UTF-8`)
        .toEntity(hello)
        .body
        .through(fs2.text.utf8.decode)
        .compile
        .string,
      """<?xml version='1.0' encoding='UTF-8'?>
        |<hello name="G??nther"/>""".stripMargin,
    )
  }

  test("encode to UTF-16") {
    val hello = <hello name="G??nther"/>
    assertIO(
      xmlEncoder(Charset.`UTF-16`)
        .toEntity(hello)
        .body
        .through(decodeWithCharset[IO](StandardCharsets.UTF_16))
        .compile
        .string,
      """<?xml version='1.0' encoding='UTF-16'?>
        |<hello name="G??nther"/>""".stripMargin,
    )
  }

  test("encode to ISO-8859-1") {
    val hello = <hello name="G??nther"/>
    assertIO(
      xmlEncoder(Charset.`ISO-8859-1`)
        .toEntity(hello)
        .body
        .through(decodeWithCharset[IO](StandardCharsets.ISO_8859_1))
        .compile
        .string,
      """<?xml version='1.0' encoding='ISO-8859-1'?>
        |<hello name="G??nther"/>""".stripMargin,
    )
  }

  property("encoder sets charset of Content-Type") {
    forAll { (cs: Charset) =>
      assertEquals(xmlEncoder(cs).headers.get[`Content-Type`].flatMap(_.charset), Some(cs))
    }
  }

  private def encodingTest(bytes: Chunk[Byte], contentType: String, name: String) = {
    val body = Stream.chunk(bytes)
    val msg = Request[IO](Method.POST, headers = Headers(Header.Raw(ci"Content-Type", contentType)))
      .withBodyStream(body)
    msg.as[Elem].map(_ \\ "hello" \@ "name").assertEquals(name)
  }

  test("parse UTF-8 charset with explicit encoding") {
    // https://datatracker.ietf.org/doc/html/rfc7303#section-8.1
    encodingTest(
      Chunk.array(
        """<?xml version="1.0" encoding="utf-8"?><hello name="G??nther"/>""".getBytes(
          StandardCharsets.UTF_8
        )
      ),
      "application/xml; charset=utf-8",
      "G??nther",
    )
  }

  test("parse UTF-8 charset with no encoding") {
    // https://datatracker.ietf.org/doc/html/rfc7303#section-8.1
    encodingTest(
      Chunk.array(
        """<?xml version="1.0"?><hello name="G??nther"/>""".getBytes(StandardCharsets.UTF_8)
      ),
      "application/xml; charset=utf-8",
      "G??nther",
    )
  }

  test("parse UTF-16 charset with explicit encoding") {
    // https://datatracker.ietf.org/doc/html/rfc7303#section-8.2
    encodingTest(
      Chunk.array(
        """<?xml version="1.0" encoding="utf-16"?><hello name="G??nther"/>""".getBytes(
          StandardCharsets.UTF_16
        )
      ),
      "application/xml; charset=utf-16",
      "G??nther",
    )
  }

  test("parse UTF-16 charset with no encoding") {
    // https://datatracker.ietf.org/doc/html/rfc7303#section-8.2
    encodingTest(
      Chunk.array(
        """<?xml version="1.0"?><hello name="G??nther"/>""".getBytes(StandardCharsets.UTF_16)
      ),
      "application/xml; charset=utf-16",
      "G??nther",
    )
  }

  test("parse omitted charset and 8-Bit MIME Entity") {
    // https://datatracker.ietf.org/doc/html/rfc7303#section-8.3
    encodingTest(
      Chunk.array(
        """<?xml version="1.0" encoding="iso-8859-1"?><hello name="G??nther"/>""".getBytes(
          StandardCharsets.ISO_8859_1
        )
      ),
      "application/xml",
      "G??nther",
    )
  }

  test("parse omitted charset and 16-Bit MIME Entity") {
    // https://datatracker.ietf.org/doc/html/rfc7303#section-8.4
    encodingTest(
      Chunk.array(
        """<?xml version="1.0" encoding="utf-16"?><hello name="G??nther"/>""".getBytes(
          StandardCharsets.UTF_16
        )
      ),
      "application/xml",
      "G??nther",
    )
  }

  test("parse omitted charset, no internal encoding declaration") {
    // https://datatracker.ietf.org/doc/html/rfc7303#section-8.5
    encodingTest(
      Chunk.array(
        """<?xml version="1.0"?><hello name="G??nther"/>""".getBytes(StandardCharsets.UTF_8)
      ),
      "application/xml",
      "G??nther",
    )
  }

  test("parse utf-16be charset") {
    // https://datatracker.ietf.org/doc/html/rfc7303#section-8.6
    encodingTest(
      Chunk.array(
        """<?xml version="1.0"?><hello name="G??nther"/>""".getBytes(StandardCharsets.UTF_16BE)
      ),
      "application/xml; charset=utf-16be",
      "G??nther",
    )
  }

  test("parse non-utf charset") {
    // https://datatracker.ietf.org/doc/html/rfc7303#section-8.7
    encodingTest(
      Chunk.array(
        """<?xml version="1.0" encoding="iso-2022-kr"?><hello name="?????????"/>""".getBytes(
          "iso-2022-kr"
        )
      ),
      "application/xml; charset=iso-2022kr",
      "?????????",
    )
  }

  test("parse conflicting charset and internal encoding") {
    // https://datatracker.ietf.org/doc/html/rfc7303#section-8.8
    encodingTest(
      Chunk.array(
        """<?xml version="1.0" encoding="utf-8"?><hello name="G??nther"/>""".getBytes(
          StandardCharsets.ISO_8859_1
        )
      ),
      "application/xml; charset=iso-8859-1",
      "G??nther",
    )
  }
}
