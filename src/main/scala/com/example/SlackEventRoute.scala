package com.example

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import io.circe.Json
import io.circe.parser._

trait SlackEventRoute extends JsonSupport {
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[String])

  val slackClient = SlackClient()

  lazy val slackEventRoute: Route =
    pathPrefix("slack") {
      pathPrefix("event") {
        pathEnd {
          post {
            entity(as[String]) { string =>
              val json = parse(string).right.getOrElse(Json.Null)
              log.info(string)

              if ((json \\ "type").headOption.flatMap(_.asString).getOrElse("") == "url_verification") {
                log.info("challenge")
                complete(slackClient.verifyToken(json))
              } else {
                log.info("else")

                val username = (json \\ "username").headOption.flatMap(_.asString).getOrElse("")
                if (username != "forgetter") {
                  val eventualJson = slackClient.postMessage("slacktest", "response")

                  onSuccess(eventualJson) { _ =>
                    log.info("success")
                    complete("Ok")
                  }
                } else {
                  complete("Ok")
                }
              }
            }
          }
        }
      }
    }
}
