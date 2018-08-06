package actors

import actors.AgentHandler.InitAgent
import akka.actor.{Actor, Props}
import config.AppConf
import db.dao.AgentDAO
import db.model.{Agent}
import protocols.Contract
import spray.http.StatusCodes
import spray.routing.RequestContext
import utils.SenzLogger

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object AgentHandler {

  case class InitAgent(agent: Agent)

  def props(requestContext: RequestContext, agent: Agent): Props = Props(new AgentHandler(requestContext, agent))
}

class AgentHandler(requestContext: RequestContext, agent: Agent) extends Actor with AppConf with SenzLogger {

  import context._
  import AgentHandler._
  import protocols.ContractProtocol._

  // send init trans to self
  self ! InitAgent(agent)

  override def receive: Receive = {
    case InitAgent(a) =>
      // check agent exists
      Try {
        Await.result(AgentDAO.create(Agent(agent.account, agent.branch)), 5.seconds)
      } match {
        case Success(_) =>
          val senz = s"DATA #status 201 @${agent.account} ^$senzieName DIGSIG"
          requestContext.complete(Contract("uid", senz))
        case Failure(e) =>
          logError(e)
          requestContext.complete(StatusCodes.BadRequest -> "400")
      }
  }
}
