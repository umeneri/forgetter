package com.example

import io.circe._, io.circe.generic.semiauto._

final case class ChallengeEvent(token: String, challenge: String, `type`: String)

final case class CallbackEvent(token: String,
                               team_id: String,
                               api_app_id: String,
                               event: MessageSlackEvent,
                               `type`: String,
                               authed_teams: Option[Seq[String]],
                               authed_users: Option[Seq[String]],
                               event_id: String,
                               event_time: Long)

sealed trait SlackEvent

case class MessageSlackEvent(
                                `type`: String,
                                event_ts: String,
                                user: String,
                                ts: String,
                                channel: String,
                                channel_type: String,
                                text: String)
  extends SlackEvent

trait ChallengeEventFormatter {
  implicit val ChallengeEventDecoder: Decoder[ChallengeEvent] = deriveDecoder[ChallengeEvent]
  implicit val ChallengeEventEncoder: Encoder[ChallengeEvent] = deriveEncoder[ChallengeEvent]
}

trait CallbackEventFormatter {
  implicit val MessageSlackEventDecoder: Decoder[MessageSlackEvent] = deriveDecoder[MessageSlackEvent]
  implicit val MessageSlackEventEncoder: Encoder[MessageSlackEvent] = deriveEncoder[MessageSlackEvent]
  implicit val CallbackEventDecoder: Decoder[CallbackEvent] = deriveDecoder[CallbackEvent]
  implicit val CallbackEventEncoder: Encoder[CallbackEvent] = deriveEncoder[CallbackEvent]
}

