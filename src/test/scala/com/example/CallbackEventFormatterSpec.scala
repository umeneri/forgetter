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
        text = "Live long and prospect."
      )

      event.right.get shouldBe expected
    }

    "decode" in {
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
          channel_type = "channel"
        ),
        `type` = "event_callback",
        authed_teams = Some(Seq("T061EG9R6")),
        authed_users = None,
        event_id = "Ev0PV52K21",
        event_time = 1355517523
      )

      event.right.get shouldBe expected
    }
  }

}
