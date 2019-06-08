package com.example

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

trait ExampleRoute extends FailFastCirceSupport {
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[String])

  lazy val exampleRoute: Route = { ctx =>
    println(ctx.request)
    ctx.complete("yes")
  }
}
