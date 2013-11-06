package com.github.athieriot.jexter

import org.fusesource.scalate.{Binding, Template, TemplateSource, TemplateEngine}
import org.fusesource.scalate.util.{UriResource, ResourceLoader}

class CustomUriTemplateSource(uri: String, resourceLoader: ResourceLoader) extends UriResource(uri, resourceLoader) with TemplateSource {

  override protected def extractPackageAndClassNames(uri: String): (String, String) = {

    super.extractPackageAndClassNames(uri) match {
      case (s: String, ct) if !s.isEmpty => (s.replace("var.", ""), ct)
      case p => p
    }
  }
}

class CustomTemplateEngine extends TemplateEngine {

  override def load(uri: String, extraBindings: Traversable[Binding]): Template = {

    load(new CustomUriTemplateSource(uri, resourceLoader), extraBindings)
  }
}
