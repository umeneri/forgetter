package com.example

import io.circe.Json
import org.scalatest.concurrent.{PatienceConfiguration, ScalaFutures}
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Matchers, WordSpec}

class SlackClientSpec extends WordSpec with Matchers with ScalaFutures {
  val slackClient = SlackClient()
  val limit: PatienceConfiguration.Timeout = timeout(Span(3, Seconds))

  "SlackClient" should {
    "test api" in {

      val res = slackClient.testApi().futureValue(limit)

      res shouldBe true
    }

    "post message" in {
      val res: Json = slackClient.postMessage("test").futureValue(limit)

      val ok = res.\\("ok").head.asBoolean.get
      ok shouldBe true

      val text = res.hcursor.downField("message").downField("text").as[String].right.get
      text shouldBe "test"
    }
  }
}