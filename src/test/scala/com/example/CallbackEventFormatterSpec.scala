package com.example

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import io.circe.parser.decode

class CallbackEventFormatterSpec extends WordSpec
  with Matchers
  with ScalaFutures
  with ScalatestRouteTest
  with SlackEventRoute
  with MockitoSugar
  with ArgumentMatchersSugar
  with CallbackEventFormatter {

  "CallbackEventFormatterSpec" should {
    "decode SlackEvent" in {
      val slackEventEntity =
        """
          |    {
          |        "type": "message",
          |        "channel": "C024BE91L",
          |        "user": "U2147483697",
          |        "text": "Live long and prospect.",
          |        "ts": "1355517523.000005",
          |        "event_ts": "1355517523.000005",
          |        "channel_type": "channel"
          |    }
          |""".stripMargin

      val event = decode[SlackEvent](slackEventEntity)

      val expected = MessageSlackEvent(
        `type` = "message",
        event_ts = "1355517523.000005",
        user = "U2147483697",
        ts = "1355517523.000005",
        channel = "C024BE91L",
        channel_type = "channel",
        text = "Live long and prospect.")

      event.right.get shouldBe expected
    }

    "decode message" in {
      val slackEventEntity =
        """
        {
          |    "token": "one-long-verification-token",
          |    "team_id": "T061EG9R6",
          |    "api_app_id": "A0PNCHHK2",
          |    "event": {
          |        "type": "message",
          |        "channel": "C024BE91L",
          |        "user": "U2147483697",
          |        "text": "Live long and prospect.",
          |        "ts": "1355517523.000005",
          |        "event_ts": "1355517523.000005",
          |        "channel_type": "channel"
          |    },
          |    "type": "event_callback",
          |    "authed_teams": [
          |        "T061EG9R6"
          |    ],
          |    "event_id": "Ev0PV52K21",
          |    "event_time": 1355517523
          |}
          |""".stripMargin

      val event = decode[CallbackEvent](slackEventEntity)

      val expected = CallbackEvent(
        token = "one-long-verification-token",
        team_id = "T061EG9R6",
        api_app_id = "A0PNCHHK2",
        event = MessageSlackEvent(
          `type` = "message",
          channel = "C024BE91L",
          user = "U2147483697",
          text = "Live long and prospect.",
          ts = "1355517523.000005",
          event_ts = "1355517523.000005",
          channel_type = "channel"),
        `type` = "event_callback",
        authed_teams = Some(Seq("T061EG9R6")),
        authed_users = None,
        event_id = "Ev0PV52K21",
        event_time = 1355517523)

      event.right.get shouldBe expected
    }

    "decode bot message" in {
      val botMessageSlackEventRaw =
        """
          |{
          |    "token": "ihh0xkJmlRdlmsTH8wSbtTZJ",
          |    "team_id": "TEGP2017V",
          |    "api_app_id": "AJ9KTJNE7",
          |    "event": {
          |        "type": "message",
          |        "subtype": "bot_message",
          |        "text": "aaa",
          |        "ts": "1560580767.001000",
          |        "username": "forgetter",
          |        "bot_id": "BJTL4THAM",
          |        "channel": "CEGP204QK",
          |        "event_ts": "1560580767.001000",
          |        "channel_type": "channel"
          |    },
          |    "type": "event_callback",
          |    "event_id": "EvKKL7E56G",
          |    "event_time": 1560580767,
          |    "authed_users": [
          |        "UK261L37G"
          |    ]
          |}
        """.stripMargin

      val expected = CallbackEvent(
        token = "ihh0xkJmlRdlmsTH8wSbtTZJ",
        team_id = "TEGP2017V",
        api_app_id = "AJ9KTJNE7",
        event = BotMessageSlackEvent(
          `type` = "message",
          subtype = "bot_message",
          channel = "CEGP204QK",
          username = "forgetter",
          bot_id = "BJTL4THAM",
          text = "aaa",
          ts = "1560580767.001000",
          event_ts = "1560580767.001000",
          channel_type = "channel"),
        `type` = "event_callback",
        authed_teams = None,
        authed_users = Some(Seq("UK261L37G")),
        event_id = "EvKKL7E56G",
        event_time = 1560580767)

      val json = decode[CallbackEvent](botMessageSlackEventRaw).right.get
      json shouldBe expected

    }
  }

}
