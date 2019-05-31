package com.example

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class SlackEventRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
    with SlackEventRoute {
  
  lazy val routes: Route = slackEventRoute

  "SlackEventRoute" should {

    "verification" in {
      val slackEventEntity = """{"token":"Jhj5dZrVaK7ZwHHjRyZWjbDl","challenge":"3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P","type":"url_verification"}"""
      val request = Post("/slack/event").withEntity(slackEventEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        entityAs[String] should ===("3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P")
      }
    }

    "verification" in {
      val slackEventEntity = """{"token":"Jhj5dZrVaK7ZwHHjRyZWjbDl","challenge":"3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P","type":"url_verification"}"""
      val request = Post("/slack/event").withEntity(slackEventEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        entityAs[String] should ===("3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P")
      }
    }
  }
}


