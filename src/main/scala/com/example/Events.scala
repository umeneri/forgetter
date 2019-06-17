package com.example

import io.circe._, io.circe.generic.semiauto._

final case class ChallengeEvent(token: String, challenge: String, `type`: String)

final case class CallbackEvent(token: String,
                               team_id: String,
                               api_app_id: String,
                               event: SlackEvent,
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

case class BotMessageSlackEvent(
                                   `type`: String,
                                   subtype: String,
                                   text: String,
                                   ts: String,
                                   username: String,
                                   bot_id: String,
                                   channel: String,
                                   event_ts: String,
                                   channel_type: String,
                               )
  extends SlackEvent


trait ChallengeEventFormatter {
  implicit val ChallengeEventDecoder: Decoder[ChallengeEvent] = deriveDecoder[ChallengeEvent]
  implicit val ChallengeEventEncoder: Encoder[ChallengeEvent] = deriveEncoder[ChallengeEvent]
}

trait CallbackEventFormatter {
  implicit val SlackEventEncoder: Encoder[SlackEvent] = deriveEncoder[SlackEvent]
  implicit val BotMessageSlackEventDecoder: Decoder[BotMessageSlackEvent] = deriveDecoder[BotMessageSlackEvent]
  implicit val BotMessageSlackEventEncoder: Encoder[BotMessageSlackEvent] = deriveEncoder[BotMessageSlackEvent]
  implicit val MessageSlackEventDecoder: Decoder[MessageSlackEvent] = deriveDecoder[MessageSlackEvent]
  implicit val MessageSlackEventEncoder: Encoder[MessageSlackEvent] = deriveEncoder[MessageSlackEvent]
  implicit val CallbackEventEncoder: Encoder[CallbackEvent] = deriveEncoder[CallbackEvent]
  implicit val CallbackEventDecoder: Decoder[CallbackEvent] = deriveDecoder[CallbackEvent]

  implicit val SlackEventDecoder: Decoder[SlackEvent] = (c: HCursor) => {
    c.downField("type").as[String].right.getOrElse("") match {
      case "message" =>
        c.downField("subtype").as[String].right.getOrElse("") match {
        case "bot_message" => c.as[BotMessageSlackEvent]
        case _ => c.as[MessageSlackEvent]
      }
      case _ => c.as[MessageSlackEvent]
    }
  }
}

