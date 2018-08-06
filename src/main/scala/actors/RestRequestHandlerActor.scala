package actors

import akka.actor.{Actor, Props}
import akka.event.slf4j.SLF4JLogging
import spray.routing.RequestContext

object RestRequestHandlerActor {

  case class Get(id: Option[Int], name: Option[String], docType: Option[String], from: Option[String], to: Option[String])

  case class Post(doc: String)

  case class Put(id: Int, doc: String)

  def props(requestContext: RequestContext) = Props(classOf[RestRequestHandlerActor], requestContext)

}

class RestRequestHandlerActor(requestContext: RequestContext) extends Actor with SLF4JLogging {

  import RestRequestHandlerActor._

  override def receive = {
    case Get(None, name, docType, from, to) =>
      // search docs
      // all docs
      log.info(s"GET document $name $docType $from $to")
      requestContext.complete("GET doc")

      context.stop(self)
    case Get(Some(id), None, None, None, None) =>
      // get specific doc
      log.info(s"GET document $id")
      requestContext.complete("GET doc HAHA")

      context.stop(self)
    case Post(doc) =>
      // create doc
      log.info(s"POST document")
      requestContext.complete("POST doc HAHA")

      context.stop(self)
    case Put(id, doc) =>
      // update doc
      log.info(s"PUT document $id")
      requestContext.complete("PUT doc HAHA")

      context.stop(self)
  }
}

