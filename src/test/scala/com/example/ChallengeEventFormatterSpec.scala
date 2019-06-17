package com.example

import akka.http.scaladsl.testkit.ScalatestRouteTest
import io.circe.parser.decode
import org.mockito.{ ArgumentMatchersSugar, MockitoSugar }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpec }

class ChallengeEventFormatterSpec extends WordSpec
  with Matchers
  with ScalaFutures
  with ScalatestRouteTest
  with SlackEventRoute
  with MockitoSugar
  with ArgumentMatchersSugar {

  "ChallengeEventFormatter" should {
    "decode" in {
      val slackEventEntity = """{"token":"Jhj5dZrVaK7ZwHHjRyZWjbDl","challenge":"3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P","type":"url_verification"}"""
      val event = decode[ChallengeEvent](slackEventEntity).right.get
      val expected = ChallengeEvent("Jhj5dZrVaK7ZwHHjRyZWjbDl", "3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P", "url_verification")
      event shouldBe expected
    }
  }

}
