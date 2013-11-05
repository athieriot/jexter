package com.github.athieriot.jexter

import akka.actor.Actor
import spray.routing._
import spray.http._

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

  val optionalContentType = optionalHeaderValuePF { case HttpHeaders.`Content-Type`(ct) => ct }

  val fakingRoute =
    path(rootPath / Rest) { path =>
      get {
        parameterSeq { params =>
          optionalContentType { contentType =>

            val definitivePath = resolvePathWithExtension(rootPath, path, contentType)

            findSupportedFile(definitivePath) match {
              case None =>                      complete(NotFound)
              case Some(("File", file)) =>      getFromFile(file)
              case Some(("Template", file)) =>  getFromTemplate(file, params.toMap)
            }
          }
        }
      }
    }

  def findSupportedFile(path: String): Option[(String, File)] = {

    //TODO: Cleaning? Or at least make clearer why
    val map = ("" :: supportedFormat).map(s => {
      (s, FileResourceLoader().resource(path ++ s))
    })
    map.filter(_._2.isDefined)
    match {
      case Nil => None
      case (format, resource) :: tail => {
        //TODO: File and Template shouldn't be just Strings
        // - Resolvers
        // - Just types
        val resolution = if (format == "") "File" else "Template"

        Some((resolution, resource.get.toFile.get))
      }
    }
  }

  private def resolvePathWithExtension(rootPath: String, path: String, ct: Option[ContentType]) = {
    val requestedExt = resolveExtension(ct)
    val approximatePath = s"$rootPath/$path"

    if (approximatePath.endsWith(requestedExt)) approximatePath
    else                                        s"$approximatePath.$requestedExt"

  }

  private def resolveExtension(ct: Option[ContentType]) = ct match {
    //TODO: More formats extensions
    case Some(e) if e.mediaType == MediaTypes.`application/json`  => "json"
    case Some(e) if e.mediaType == MediaTypes.`text/xml`          => "xml"
    case _ => ""
  }
}
