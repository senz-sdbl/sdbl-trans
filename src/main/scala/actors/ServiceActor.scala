package actors

import akka.actor.{Actor, Props}
import protocols.{Contract, Senz, SenzType}
import spray.http.StatusCodes
import spray.routing.HttpService
import utils._

import scala.util.{Success, Try}


object ServiceActor {
  def props() = Props(classOf[ServiceActor])
}

class ServiceActor extends Actor with RestService {
  implicit def actorRefFactory = context

  override def receive = runRoute(router)
}

trait RestService extends HttpService with SenzLogger {
  implicit def executionContext = actorRefFactory.dispatcher

  val router = {
    pathPrefix("api" / "v1") {
      path("contracts") {
        import protocols.ContractProtocol._
        post {
          entity(as[Contract]) { contract =>
            requestContext =>
              logger.info(s"POST contract $contract")
              val senz = Try(SenzParser.parseSenz(contract.senz))
              senz match {
                case Success(z@Senz(SenzType.SHARE, _, _, _, _)) =>
                  //requestContext.complete(StatusCodes.Created -> "201")
                  requestContext.complete(Contract("uid", "DATA #status 201 @agent ^switch digsig;"))
                case Success(z@Senz(SenzType.PUT, _, _, _, _)) =>
                  // trans
                  val trans = TransUtils.getTrans(z)
                  actorRefFactory.actorOf(TransHandler.props(requestContext, trans))
                case Success(z@Senz(SenzType.GET, _, _, attr, _)) =>
                  // inq
                  if (attr.contains("#acc") && attr.contains("#nic")) {
                    // acc inq
                    val accInq = AccInquiryUtils.getAccInq(z)
                    actorRefFactory.actorOf(AccInqHandler.props(requestContext, accInq))
                  } else if (attr.contains("#bal") && attr.contains("#acc")) {
                    // bal inq
                    val balInq = BalInqUtils.getBalInq(z)
                    actorRefFactory.actorOf(BalInqHandler.props(requestContext)) ! balInq
                  }
                case _ =>
                  logger.debug(s"Not support message: ${contract.senz}")
                  requestContext.complete(StatusCodes.BadRequest -> "400")
              }
          }
        }
      }
    }
  }
}

