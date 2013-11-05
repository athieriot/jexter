package com.github.athieriot.jexter

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class FakeDataServiceSpec extends Specification with Specs2RouteTest with FakeDataService {
  def actorRefFactory = system
  
  "FakeDataService" should {

    //TODO: Test when:
    // - Header Content type + extension
    // - Header Content type without extension
    // - No header + No extension
    "return a static JSON corresponding to the given path" in {
      Get("/data/order.json") ~> fakingRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`application/json`)
        responseAs[String] must contain("order")
      }
    }

    "return a json generate by a SSP template" in {
      Get("/data/projects.json?city=Verdun") ~> fakingRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`application/json`)
        responseAs[String] must contain("Verdun")
      }
    }

    "return a json generate by a Mustache template" in {
      Get("/data/order/details.json?city=Paris") ~> fakingRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`application/json`)
        responseAs[String] must contain("Paris")
      }
    }

    "return a json generate by a SSP template with conditional" in {
      Get("/data/capitol.json") ~> fakingRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`application/json`)
        responseAs[String] must not contain("Rome")
        responseAs[String] must not contain("Paris")
      }
      Get("/data/capitol.json?country=France") ~> fakingRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`application/json`)
        responseAs[String] must contain("Paris")
        responseAs[String] must not contain("Rome")
      }
      Get("/data/capitol.json?country=Italia") ~> fakingRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`application/json`)
        responseAs[String] must contain("Rome")
        responseAs[String] must not contain("Paris")
      }
    }

    "return a XML corresponding to the given path" in {
      Get("/data/order/example.xml") ~> fakingRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`text/xml`)
        responseAs[String] must contain("batch")
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
  }
}
