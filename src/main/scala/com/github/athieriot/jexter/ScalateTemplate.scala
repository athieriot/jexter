package com.github.athieriot.jexter

import org.fusesource.scalate.{TemplateException, TemplateEngine}
import org.fusesource.scalate.util.Resource
import java.io.File
import spray.routing.directives.ContentTypeResolver
import spray.routing._
import spray.routing.directives.MethodDirectives._
import spray.routing.directives.RouteDirectives._
import org.fusesource.scalate.util.FileResourceLoader
import scala.Some
import spray.http.{HttpData, HttpEntity}


trait ScalateTemplate {
  import com.typesafe.config.{ConfigFactory, Config}

  val PACKAGE_PREFIX_CONF_PATH: String = "jexter.packagePrefix"

  lazy val engine = {
    val engine = new TemplateEngine

    setPackagePrefix(engine)

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

        complete(HttpEntity(resolver.apply(fileName.stripSuffix(s".${ext}")), HttpData(output)))
      } catch {
        case e: TemplateException => reject
      }
    }
  }

  private def setPackagePrefix(engine: TemplateEngine): Unit = {
    val conf: Config = ConfigFactory.load()

    if (conf.hasPath(PACKAGE_PREFIX_CONF_PATH)
      && !conf.getString(PACKAGE_PREFIX_CONF_PATH).isEmpty) {

      engine.packagePrefix = conf.getString(PACKAGE_PREFIX_CONF_PATH)
    }
  }
}