package forgetter

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Json

import scala.concurrent.Future

trait SlackEventRoute extends FailFastCirceSupport
  with ChallengeEventFormatter
  with CallbackEventFormatter {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[String])

  val slackClient = SlackClient()

  lazy val slackEventRoute: Route =
    pathPrefix("slack") {
      pathPrefix("event") {
        pathEnd {
          post {
            entity(as[ChallengeEvent]) { event =>
              complete(slackClient.verifyChallengeToken(event))
            } ~
              entity(as[CallbackEvent]) { callbackEvent =>
                callbackEvent.event match {
                  case v: MessageSlackEvent =>
                    onSuccess(replyEchoResponse(v)) { _ => complete("Ok") }
                  case _: BotMessageSlackEvent => complete("Ok")
                }
              }
          }
        }
      }
    }

  private def replyEchoResponse(event: MessageSlackEvent): Future[Json] = {
    val testChannel = "slacktest"
    slackClient.postMessage(channel = testChannel, text = event.text)
  }
}
