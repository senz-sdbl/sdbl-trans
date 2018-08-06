package actors

import java.net.{InetAddress, InetSocketAddress}

import actors.AccInqHandler.AccInq
import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import config.AppConf
import org.slf4j.LoggerFactory
import protocols.Contract
import spray.http.StatusCodes
import spray.routing.RequestContext
import utils.AccInquiryUtils

import scala.concurrent.duration._

object AccInqHandler {

  case class AccInqMsg(msgStream: Array[Byte])

  case class AccInqResp(esh: String, status: String, auth: String, accs: String)

  case class AccInq(agent: String, nic: String)

  case class AccInqTimeout()

  def props(requestContext: RequestContext, accInq: AccInq): Props = Props(new AccInqHandler(requestContext, accInq))

}

class AccInqHandler(requestContext: RequestContext, accInq: AccInq) extends Actor with AppConf {

  import AccInqHandler._
  import context._
  import protocols.ContractProtocol._

  def logger = LoggerFactory.getLogger(this.getClass)

  // connect to epic tcp end
  val remoteAddress = new InetSocketAddress(InetAddress.getByName(epicHost), epicPort)
  IO(Tcp) ! Connect(remoteAddress)

  // handle timeout in 30 seconds
  var timeoutCancellable = system.scheduler.scheduleOnce(30.seconds, self, AccInqTimeout())

  override def preStart(): Unit = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case Connected(_, _) =>
      logger.debug("TCP connected")

      // inqMsg from
      val inqMsg = AccInquiryUtils.getAccInqMsg(accInq)
      val msgStream = new String(inqMsg.msgStream)

      logger.debug("Send AccInq: " + msgStream)

      // send AccInq
      val connection = sender()
      connection ! Register(self)
      connection ! Write(ByteString(msgStream))

      // handler response
      context become {
        case CommandFailed(_: Write) =>
          logger.error("CommandFailed[Failed to write]")
        case Received(data) =>
          val response = data.decodeString("UTF-8")
          logger.debug("Response received: " + response)

          // cancel timer
          timeoutCancellable.cancel()

          handleResponse(response, connection)
        case _: ConnectionClosed =>
          logger.error("ConnectionClosed before complete the trans")

          // cancel timer
          timeoutCancellable.cancel()

          // send error back
          requestContext.complete(StatusCodes.BadRequest -> "400")
        case AccInqTimeout() =>
          // timeout
          logger.error("acc inq timeout")

          // send error status back
          requestContext.complete(StatusCodes.BadRequest -> "400")
      }
    case CommandFailed(_: Connect) =>
      // failed to connect
      logger.error("CommandFailed[Failed to connect]")

      // cancel timer
      timeoutCancellable.cancel()

      // send error status back
      val senz = s"DATA #status ERROR @${accInq.agent} ^$senzieName"
      requestContext.complete(StatusCodes.BadRequest -> "400")
  }

  def handleResponse(response: String, connection: ActorRef): Unit = {
    // parse response and get 'acc response'
    AccInquiryUtils.getAccInqResp(response) match {
      case AccInqResp(_, "00", _, data) =>
        logger.debug(s"acc inq done $data")

        // parse acc response and find accounts
        val ans = (for (an <- data.split("~")) yield {
          val t = an.split("#")
          t(1).trim + "|" + t(2).trim
        }).mkString(",")
        logger.debug(s"ans: $data")

        // send response back
        val senz = s"DATA #acc ${ans.trim.replaceAll(" ", "_")} @${accInq.agent} ^$senzieName"
        requestContext.complete(Contract("uid", senz))
      case AccInqResp(_, "11", _, _) =>
        logger.error(s"No account found for id ${accInq.nic}")

        // send empty response back
        val senz = s"DATA #acc @${accInq.agent} ^$senzieName"
        requestContext.complete(Contract("uid", senz))
      case AccInqResp(_, status, _, _) =>
        logger.error("acc inq fail with stats: " + status)

        // send empty response back
        requestContext.complete(StatusCodes.BadRequest -> "400")
      case resp =>
        logger.error("invalid response " + resp)

        // send error status back
        requestContext.complete(StatusCodes.BadRequest -> "400")
    }

    context.stop(self)
  }

}
