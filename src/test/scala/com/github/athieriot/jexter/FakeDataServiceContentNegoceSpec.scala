package com.github.athieriot.jexter

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import spray.http.HttpHeaders._
import spray.http.MediaTypes._
import spray.http.StatusCodes._

class FakeDataServiceContentNegoceSpec extends Specification with Specs2RouteTest with FakeDataService {
  def actorRefFactory = system
  
  "FakeDataService" should {

    "return a static JSON corresponding to the given extension" in {
      Get("/data/order.json") ~> addHeader(`Content-Type`(`application/json`)) ~> fakingRoute ~> check {
        mediaType must beEqualTo(`application/json`)
        responseAs[String] must contain("order")
      }
    }

    "return a static JSON corresponding to the given Content Type" in {
      Get("/data/order") ~> addHeader(`Content-Type`(`application/json`)) ~> fakingRoute ~> check {
        mediaType must beEqualTo(`application/json`)
        responseAs[String] must contain("order")
      }
    }

    "return a XML corresponding to the given given Content Type" in {
      Get("/data/order/example") ~> addHeader(`Content-Type`(`text/xml`)) ~> fakingRoute ~> check {
        mediaType must beEqualTo(`text/xml`)
        responseAs[String] must contain("batch")
      }
    }

    "return a plain text content if no extension nor Content Type is given" in {
      Get("/data/order/ignoreme") ~> fakingRoute ~> check {
        mediaType must beEqualTo(`application/octet-stream`)
        responseAs[String] must contain("NOT")
      }
    }
  }
}
