package forgetter

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import io.circe.Json
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import org.mockito.MockitoSugar
import org.mockito.ArgumentMatchersSugar

import scala.concurrent.Future

class SlackEventRoutesSpec extends WordSpec
  with Matchers
  with ScalaFutures
  with ScalatestRouteTest
  with SlackEventRoute
  with MockitoSugar
  with ArgumentMatchersSugar {

  lazy val routes: Route = slackEventRoute
  override val slackClient: SlackClient = mock[SlackClient]
  when(slackClient.verifyChallengeToken(any[ChallengeEvent])).thenReturn("3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P")
  when(slackClient.postMessage(any[String], any[String])).thenReturn(Future.successful(Json.Null))

  "SlackEventRoute" should {

    "verification" in {
      val slackEventEntity = """{"token":"Jhj5dZrVaK7ZwHHjRyZWjbDl","challenge":"3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P","type":"url_verification"}"""
      val request = Post("/slack/event").withEntity(ContentTypes.`application/json`, slackEventEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        entityAs[String] should ===("3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P")
      }
    }

    "response text" in {
      val slackEventEntity =
        """{
          |  "token" : "ihh0xkJmlRdlmsTH8wSbtTZJ",
          |  "team_id" : "TEGP2017V",
          |  "api_app_id" : "AJ9KTJNE7",
          |  "event" : {
          |    "client_msg_id" : "81e04d5e-bf63-4c91-af45-66b95bb2289c",
          |    "type" : "message",
          |    "text" : "aeeeea",
          |    "user" : "UEGM3E220",
          |    "ts" : "1560040018.000200",
          |    "channel" : "CEGP204QK",
          |    "event_ts" : "1560040018.000200",
          |    "channel_type" : "channel"
          |  },
          |  "type" : "event_callback",
          |  "event_id" : "EvK2347UTC",
          |  "event_time" : 1560040018,
          |  "authed_users" : [
          |    "UK261L37G"
          |  ]
          |}
          |""".stripMargin

      val request = Post("/slack/event").withEntity(ContentTypes.`application/json`, slackEventEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        entityAs[String] shouldBe "Ok"
      }
    }
  }
}

