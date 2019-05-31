package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import io.circe.{ Json, ParsingFailure }
import io.circe.parser._

import scala.collection.immutable
import scala.concurrent.{ ExecutionContextExecutor, Future }
import akka.http.scaladsl.model.headers._

case class SlackClient() {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def testApi(): Future[Boolean] = {
    val endpoint = "api.test"
    val JsonEitherFuture: Future[Either[ParsingFailure, Json]] = makeRequest(endpoint)

    JsonEitherFuture.map { either =>
      either.map { json =>
        json.\\("ok").head.asBoolean.get
      }.getOrElse(false)
    }
  }

  def postMessage(channel: String, text: String): Future[Json] = {
    val endpoint = "chat.postMessage"
    val json = s"""{"channel": "$channel", "text": "$text"}"""

    makeJsonRequest(endpoint, json)
  }

  def verifyToken(json: Json): String = {
    (json \\ "challenge").headOption.flatMap(_.asString).getOrElse("")
  }

  private def makeJsonRequest(endpoint: String, json: String): Future[Json] = {
    val url = s"https://slack.com/api/$endpoint"
    val token = System.getenv("OAUTH2TOKEN")
    val request = HttpRequest(
      uri = url,
      method = HttpMethods.POST,
      entity = HttpEntity(ContentTypes.`application/json`, json),
      headers = immutable.Seq(Authorization(OAuth2BearerToken(token))))

    val JsonEitherFuture = for {
      httpResponse <- Http().singleRequest(request)
      string <- Unmarshal(httpResponse.entity).to[String]
    } yield parse(string)

    JsonEitherFuture.map { either =>
      either.getOrElse(Json.Null)
    }
  }

  private def makeRequest(endpoint: String): Future[Either[ParsingFailure, Json]] = {
    val url = s"https://slack.com/api/$endpoint"

    for {
      httpResponse <- Http().singleRequest(HttpRequest(uri = url))
      string <- Unmarshal(httpResponse.entity).to[String]
    } yield parse(string)
  }
}
