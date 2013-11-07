package com.github.athieriot.jexter

import org.fusesource.scalate.TemplateException
import org.fusesource.scalate.util.Resource
import java.io.File
import spray.routing.directives.ContentTypeResolver
import spray.routing._
import spray.routing.directives.MethodDirectives._
import spray.routing.directives.RouteDirectives._
import org.fusesource.scalate.util.FileResourceLoader
import spray.http.{StatusCodes, HttpData, HttpEntity}
import com.typesafe.scalalogging.slf4j.Logging
import com.github.athieriot.jexter.custom.CustomTemplateEngine


trait ScalateTemplate extends Logging {
  import StatusCodes._

  lazy val engine = {
    // Need to override a small number of methods in the default TemplateEngine.
    // It is hopefully a temporary solution to fix a bug happening when
    // the application is located under /var/ (Example: Jenkins build)
    val engine = new CustomTemplateEngine

    engine.resourceLoader = new FileResourceLoader {
      override def resource(uri: String): Option[Resource] =
        Some(Resource.fromUri(uri, FileResourceLoader()))
    }

    engine
  }

  def getFromTemplate(file: File, context: Map[String, Any])(implicit resolver: ContentTypeResolver): Route = {
    get {
      val fileName: String = file.getAbsolutePath
      val ext = fileName.lastIndexOf('.') match {
        case -1 ⇒ ""
        case x  ⇒ fileName.substring(x + 1)
      }

      try {
        val output = engine.layout(fileName, context)

        complete(HttpEntity(resolver.apply(fileName.stripSuffix(s".$ext")), HttpData(output)))
      } catch {
        case e: TemplateException => {

          logger.error(e.getLocalizedMessage)
          complete(BadRequest)
        }
      }
    }
  }
}