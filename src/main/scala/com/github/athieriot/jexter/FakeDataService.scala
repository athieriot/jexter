package com.github.athieriot.jexter

import akka.actor.Actor
import spray.routing._

class FakeDataServiceActor extends Actor with FakeDataService {

  def actorRefFactory = context

  def receive = runRoute(fakingRoute)
}

trait FakeDataService extends HttpService with ScalateTemplate {
  import java.io.File
  import org.fusesource.scalate.util.FileResourceLoader
  import com.typesafe.config.{Config, ConfigFactory}
  import spray.http.StatusCodes._
  import collection.JavaConversions._

  val conf: Config = ConfigFactory.load()

  // non-lazy fields, we want all exceptions at construct time
  val supportedFormat = conf.getStringList( "jexter.supportedScalateFormat").toList
  val rootPath =        conf.getString(     "jexter.rootPath")

  val fakingRoute =
    path(rootPath / Rest) { path =>
      get {
        parameterSeq { params =>

          findSupportedFile(s"$rootPath/$path") match {
            case None =>              complete(NotFound)
            case Some(("", file)) =>  getFromFile(file)
            case Some((_, file)) =>   getFromTemplate(file, params.toMap)
          }
        }
      }
    }

  def findSupportedFile(path: String): Option[(String, File)] = {
    val map = ("" :: supportedFormat).map(s => {
      (s, FileResourceLoader().resource(path ++ s))
    })
    map.filter(_._2.isDefined)
    match {
      case Nil => None
      case (format, resource) :: tail => Some((format, resource.get.toFile.get))
    }
  }
}
