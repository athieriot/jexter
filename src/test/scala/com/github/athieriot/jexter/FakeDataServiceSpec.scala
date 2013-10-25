package com.github.athieriot.jexter

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class FakeDataServiceSpec extends Specification with Specs2RouteTest with FakeDataService {
  def actorRefFactory = system
  
  "FakeDataService" should {

    "return a greeting for GET requests to the root path" in {
      Get("/data/order.json") ~> myRoute ~> check {
        contentType must beEqualTo(ContentTypes.`application/json`)
        responseAs[String] must contain("tests")
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
