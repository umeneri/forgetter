package com.example

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.circe.{Json, ParsingFailure}
import org.scalatest.{Matchers, WordSpec}

class AkkaJsonSpec extends WordSpec with Matchers {

  final case class Item(name: String, id: Long)

  final case class Order(items: List[Item])

  "Circe Json" should {
    "format to json" in {
      val item = Item("item1", 1)
      val json = item.asJson.noSpaces
      json shouldBe "{\"name\":\"item1\",\"id\":1}"
    }

    "format from json" in {
      val json = "{\"name\":\"item1\",\"id\":1}"
      val actual = decode[Item](json).right.get

      actual shouldBe Item("item1", 1)
    }

    "format slack response json to Response" in {
      val rawJson = scala.io.Source.fromResource("postMessageResponse.json").mkString
      val json: Either[ParsingFailure, Json] = parse(rawJson)
      val actual = json.right.get.\\("ok").head.asBoolean.get
      actual shouldBe true
    }
  }
}
