package com.github.athieriot.jexter

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import org.specs2.matcher.{XmlMatchers, JsonMatchers}

class FakeDataServiceSpec extends Specification with Specs2RouteTest
  with FakeDataService
  with JsonMatchers
  with XmlMatchers {

  def actorRefFactory = system
  
  "FakeDataService" should {

    "return a static JSON corresponding to the given path" in {
      Get("/data/order.json") ~> fakingRoute ~> check {
        mediaType must beEqualTo(`application/json`)
        responseAs[String] must /("title" -> "order")
      }
    }

    "return a json generate by a SSP template" in {
      Get("/data/projects.json?city=Verdun") ~> fakingRoute ~> check {
        mediaType must beEqualTo(`application/json`)
        responseAs[String] must /("city" -> "Verdun")
      }
    }

    "return a json generate by a Mustache template" in {
      Get("/data/order/details.json?city=Paris") ~> fakingRoute ~> check {
        mediaType must beEqualTo(`application/json`)
        responseAs[String] must /("city" -> "Paris")
      }
    }

    "return a json generate by a SSP template with conditional" in {
      Get("/data/capitol.json") ~> fakingRoute ~> check {
        mediaType must beEqualTo(`application/json`)
        responseAs[String] must contain("{}")
      }
      Get("/data/capitol.json?country=France") ~> fakingRoute ~> check {
        mediaType must beEqualTo(`application/json`)
        responseAs[String] must /("capitol" -> "Paris")
      }
      Get("/data/capitol.json?country=Italia") ~> fakingRoute ~> check {
        mediaType must beEqualTo(`application/json`)
        responseAs[String] must /("capitol" -> "Rome")
      }
    }

    import xml.XML

    "return a XML corresponding to the given path" in {
      Get("/data/order/example.xml") ~> fakingRoute ~> check {
        mediaType must beEqualTo(`text/xml`)
        XML.loadString(responseAs[String]) must \("metadata") \("command") \> "batch"
      }
    }

    "return a sensible status code when no resource is found to render" in {
      Get("/data/order/example.never") ~> fakingRoute ~> check {
        status must beEqualTo(NotFound)
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> fakingRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put("/data/order.json") ~> sealRoute(fakingRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }

    "return a sensible status code when template fail to generate" in {
      Get("/data/bad.json") ~> fakingRoute ~> check {
        status must beEqualTo(BadRequest)
      }
    }
  }
}
