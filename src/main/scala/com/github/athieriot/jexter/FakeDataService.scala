package com.github.athieriot.jexter

import akka.actor.Actor
import spray.routing._
import spray.http.ContentTypes

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

  val myRoute =
    path("data" / Rest) { path =>
      get {
        parameterSeq { params =>
          getFromResource("data/" + path, ContentTypes.`application/json`)
        }
      }
    }
}
