[![Travis CI Status](https://travis-ci.org/athieriot/jexter.png)](https://travis-ci.org/athieriot/jexter)

# Jexter

## Spray server for dynamic, fake REST data

This project intent to be a lightweight server to provide a fake REST interface for you application.

## Usage

Drop a file into a __data/__ directory in your classpath and it will be automatically served.

This is best deployed into a WAR file.

### Static files

Any file present will be made available as is.
The content type is deduced from the extension.

__data/your/file.json__ will be available at __http://localhost:port/data/your/file.json__ as __application/json__

### Content Negociation

You can request, via the header value "Content-Type" a specific result format.
This will order Jexter to look for a specific file extension in your classpath.

For example:

__data/your/file.json__ can be available at __http://localhost:port/data/your/file__ if you add in your header __Content-Type = "application/json"__

### Dynamic templates

Ok. This is very good but I don't need Spray just for static files!

When faking API results, you quickly need to handle parameters.
More generally, it might be handy to customise the data, generate values and so on.

Jexter allows you to use [Scalate](http://scalate.fusesource.org/) templates.

__data/your/dyno.json.mustache__ will be available at __http://localhost:port/data/your/dyno.json__

Any parameters you have in your request is passed to the template as value.

Given a template:

```
{
    "title": "{{ceci}}"
}
```

__http://localhost:port/data/your/dyno.json?ceci=cela__ will render:

```
{
    "title": "cela"
}
```

### More features coming

[https://github.com/athieriot/jexter/blob/master/TODO](https://github.com/athieriot/jexter/blob/master/TODO)

## Installation

Add the dependency:

SBT:
```
libraryDependencies += "com.github.athieriot" %% "jexter" % "0.1"
```

Maven:
```
<dependency>
    <groupId>com.github.athieriot</groupId>
    <artifactId>jexter_2.10</artifactId>
    <version>0.1</version>
</dependency>
```

Add a new __web.xml__ file in __webapp/WEB-INF__ with this content:

```
<?xml version="1.0"?>

<!DOCTYPE web-app PUBLIC "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
        "http://java.sun.com/dtd/web-app_2_4.dtd">

<web-app>

    <listener>
        <listener-class>spray.servlet.Initializer</listener-class>
    </listener>

    <servlet>
        <servlet-name>SprayConnectorServlet</servlet-name>
        <servlet-class>spray.servlet.Servlet30ConnectorServlet</servlet-class>
        <async-supported>true</async-supported>
    </servlet>

    <servlet-mapping>
        <servlet-name>SprayConnectorServlet</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>

</web-app>
```

For SBT, you will want to configure the [xsbt-web-plugin](https://github.com/JamesEarlDouglas/xsbt-web-plugin)

### Examples

This project contains two minimal projects examples to start with:

Maven: [https://github.com/athieriot/jexter/tree/master/examples/maven](https://github.com/athieriot/jexter/tree/master/examples/maven)

SBT: [https://github.com/athieriot/jexter/tree/master/examples/sbt](https://github.com/athieriot/jexter/tree/master/examples/sbt)

## Repository

One of the required dependency of this project is Spray.io.
For now (2013), Spray.io is not hosted in the Sonatype repository.

As such, Jexter can't be hosted there neither.
While this is the case, you will need to add a third party repository in your project.

SBT:
```
resolvers += "Jexter Maven Repository" at "https://raw.github.com/athieriot/jexter/master/mvn-repo"
```

Maven:
```
<repository>
    <id>jexter-repository</id>
    <url>https://raw.github.com/athieriot/jexter/master/mvn-repo</url>
</repository>
```

## Configuration

You can optionally override values in an __application.conf__ present in the classpath

### Spray


If you deploy the project as a WAR at a different context than root.
You need to specify it to Spray.
In this example, Spray can respond to http://localhost:8080/maven

```
spray.servlet {
  root-path = "/maven"
}

```

### Jexter

The root path of Jexter is where your files can be found in the class path.

```
jexter {
    rootPath = "data"
}
```

## Troubleshooting

With the default JVM configuration, you might encounter an out of memory exception using Jexter.
Increase the value of the property ```MaxPermSize``` should resolve the problem.

The __JAVA_OPTS__ option to do so is:

```
-XX:MaxPermSize=256M
```

## Thanks

Inspired from [Dyson](https://github.com/webpro/dyson)


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/athieriot/jexter/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

