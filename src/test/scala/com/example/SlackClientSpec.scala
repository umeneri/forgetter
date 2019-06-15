package com.example

import io.circe.Json
import io.circe.parser._
import org.scalatest.concurrent.{PatienceConfiguration, ScalaFutures}
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, WordSpec}

class SlackClientSpec extends WordSpec with Matchers with ScalaFutures
  with ChallengeEventFormatter {
  val slackClient = SlackClient()
  val limit: PatienceConfiguration.Timeout = timeout(Span(3, Seconds))

  "SlackClient" should {
    "test api" in {

      val res = slackClient.testApi().futureValue(limit)

      res shouldBe true
    }

    "post message" in {
      val res: Json = slackClient.postMessage("slacktest", "test").futureValue(limit)

      val ok = res.\\("ok").head.asBoolean.get
      ok shouldBe true

      val text = res.hcursor.downField("message").downField("text").as[String].right.get
      text shouldBe "test"
    }

    "challenge" in {
      val slackEventEntity = """{"token":"Jhj5dZrVaK7ZwHHjRyZWjbDl","challenge":"3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P","type":"url_verification"}"""
      val challengeEvent = decode[ChallengeEvent](slackEventEntity).right.get
      val challenge = slackClient.verifyChallengeToken(challengeEvent)
      challenge shouldBe "3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P"
    }
  }
}