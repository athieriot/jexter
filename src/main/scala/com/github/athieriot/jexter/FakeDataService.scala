package com.github.athieriot.jexter

import akka.actor.Actor
import spray.routing._
import spray.http.MediaTypes._

import org.fusesource.scalate._
import org.fusesource.scalate.util.{FileResourceLoader, Resource}
import java.io.{File, FileNotFoundException}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class FakeDataServiceActor extends Actor with FakeDataService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


// this trait defines our service behavior independently from the service actor
trait FakeDataService extends HttpService {

  def engine = {
    val engine = new TemplateEngine
    engine.resourceLoader = new FileResourceLoader {
      override def resource(uri: String): Option[Resource] =
        Some(Resource.fromUri(uri, FileResourceLoader()))
    }

    engine
  }

  val supportedFormat = List("json", "ssp", "mustache", "scaml", "jade")

  val myRoute =
    path("data" / Rest) { path =>
      get {
        parameterSeq { params =>

          findSupportedFile("data/" + path) match {
            case ("json", file) => {
              getFromFile(file, `application/json`)
            }
            case (format, file) => {
              respondWithMediaType(`application/json`) {

                complete(engine.layout(file.getAbsolutePath, params.toMap))
              }
            }
          }
        }
      }
    }

  def findSupportedFile(path: String): (String, File) = {
    val map = supportedFormat.map(s => {
      (s, FileResourceLoader().resource(path.replace("json", s)))
    })
    map.filter(_._2.isDefined)
    match {
      case Nil => throw new FileNotFoundException()
      case (format, resource) :: tail => (format, resource.get.toFile.get)
    }
  }
}
