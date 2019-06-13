package com.example

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Json

trait SlackEventRoute extends FailFastCirceSupport {
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[String])

  val slackClient = SlackClient()

  lazy val slackEventRoute: Route =
    pathPrefix("slack") {
      pathPrefix("event") {
        pathEnd {
          post {
            entity(as[Json]) {
              case json if isVerification(json) => complete(slackClient.verifyToken(json))
              case json if isUserMessage(json) =>
                val eventualJson = slackClient.postMessage("slacktest", "response")
                onSuccess(eventualJson) { json =>
                  log.info(json.toString())
                  complete("Ok")
                }
              case _ => complete("Ok")
            }
          }
        }
      }
    }

  private def isVerification(json: Json): Boolean = {
    (json \\ "type").headOption.flatMap(_.asString).getOrElse("") == "url_verification"
  }

  private def isUserMessage(json: Json) = {
    (json \\ "username").headOption.flatMap(_.asString).getOrElse("") != "forgetter"
  }
}
