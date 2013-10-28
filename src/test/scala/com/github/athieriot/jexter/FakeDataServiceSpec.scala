package com.github.athieriot.jexter

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class FakeDataServiceSpec extends Specification with Specs2RouteTest with FakeDataService {
  def actorRefFactory = system
  
  "FakeDataService" should {

    //TODO: Test JSON format
    "return a static JSON corresponding to the given path" in {
      Get("/data/order.json") ~> myRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`application/json`)
        responseAs[String] must contain("order")
      }
    }

    "return a json generate by a SSP template" in {
      Get("/data/projects.json?city=Verdun") ~> myRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`application/json`)
        responseAs[String] must contain("Verdun")
      }
    }

    "return a json generate by a Mustache template" in {
      Get("/data/order/details.json?city=Paris") ~> myRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`application/json`)
        responseAs[String] must contain("Paris")
      }
    }

    "return a json generate by a SSP template with conditional" in {
      Get("/data/capitol.json") ~> myRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`application/json`)
        responseAs[String] must not contain("Rome")
        responseAs[String] must not contain("Paris")
      }
      Get("/data/capitol.json?country=France") ~> myRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`application/json`)
        responseAs[String] must contain("Paris")
        responseAs[String] must not contain("Rome")
      }
      Get("/data/capitol.json?country=Italia") ~> myRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`application/json`)
        responseAs[String] must contain("Rome")
        responseAs[String] must not contain("Paris")
      }
    }

    "return a XML corresponding to the given path" in {
      Get("/data/order/example.xml") ~> myRoute ~> check {
        mediaType must beEqualTo(MediaTypes.`text/xml`)
        responseAs[String] must contain("batch")
      }
    }

    "return a specific message when format not supported" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put("/data/order.json") ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
  }
}
