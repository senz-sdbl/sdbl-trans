package actors

import java.net.{InetAddress, InetSocketAddress}

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import config.AppConf
import db.dao.TranDAO
import db.model.Transaction
import protocols.Contract
import spray.http.StatusCodes
import spray.routing.RequestContext
import utils.{SenzLogger, TransUtils}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object TransHandler {

  case class InitTrans(trans: Transaction)

  case class TransMsg(msgStream: Array[Byte])

  case class TransResp(esh: String, status: String, rst: String)

  case class TransTimeout()

  def props(requestContext: RequestContext, trans: Transaction): Props = Props(new TransHandler(requestContext, trans))
}

class TransHandler(requestContext: RequestContext, trans: Transaction) extends Actor with AppConf with SenzLogger {

  import TransHandler._
  import context._
  import protocols.ContractProtocol._

  // send init trans to self
  self ! InitTrans(trans)

  // handle timeout in 30 seconds
  var timeoutCancellable = system.scheduler.scheduleOnce(30.seconds, self, TransTimeout())

  override def preStart(): Unit = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case InitTrans(tr) =>
      // create transaction, if not exists
      Try {
        Await.result(TranDAO.getOrCreate(tr), 10.seconds)
      } match {
        case Success((_: Transaction, 1)) =>
          // transaction created
          // connect tcp
          // connect to epic tcp end
          logger.info(s"New trans: ${tr.uid}")
          val remoteAddress = new InetSocketAddress(InetAddress.getByName(epicHost), epicPort)
          IO(Tcp) ! Connect(remoteAddress, timeout = Option(15.seconds))
        case Success((t: Transaction, 0)) =>
          // transaction exists
          // send transaction status back
          logger.info(s"Existing trans: ${trans.uid}")
          val senz = s"DATA #uid ${trans.uid} #status ${t.status} @${trans.agent} ^sdbltrans"
          requestContext.complete(Contract(trans.uid, senz))
        case Success(r) =>
          // unexpected result
          logger.error(s"Unexpected result: $r")
        case Failure(e) =>
          logError(e)

          // something went wrong
          // send ERROR status back
          val senz = s"DATA #uid${trans.uid} #status ERROR @${trans.agent} ^$senzieName"
          requestContext.complete(Contract(trans.uid, senz))
      }
    case Connected(_, _) =>
      logger.debug("TCP connected")

      // transMsg from trans
      val transMsg = TransUtils.getTransMsg(trans)
      val msgStream = new String(transMsg.msgStream)

      logger.debug("Send TransMsg " + msgStream)

      // send TransMsg
      val connection = sender()
      connection ! Register(self)
      connection ! Write(ByteString(transMsg.msgStream))

      // handler response
      context become {
        case CommandFailed(_: Write) =>
          logger.error("CommandFailed[Failed to write]")
          timeoutCancellable.cancel()

          val senz = s"DATA #uid${trans.uid} #status ERROR @${trans.agent} ^$senzieName"
          requestContext.complete(Contract(trans.uid, senz))
        case Received(data) =>
          val response = data.decodeString("UTF-8")
          logger.debug("Received : " + response)

          // cancel timer
          timeoutCancellable.cancel()

          handleResponse(response, connection)
        case _: ConnectionClosed =>
          logger.error("ConnectionClosed before complete the trans")

          // cancel timer
          timeoutCancellable.cancel()

          // send error response
          val senz = s"DATA #uid${trans.uid} #status ERROR @${trans.agent} ^$senzieName"
          requestContext.complete(Contract(trans.uid, senz))
        case TransTimeout() =>
          // timeout
          logger.error("TransTimeout")

          // send error response
          val senz = s"DATA #uid${trans.uid} #status ERROR @${trans.agent} ^$senzieName"
          requestContext.complete(Contract(trans.uid, senz))
      }
    case CommandFailed(_: Connect) =>
      // failed to connect
      logger.error("CommandFailed[Failed to connect]")

      // cancel timer
      timeoutCancellable.cancel()

      // send fail status back
      val senz = s"DATA #uid${trans.uid} #status ERROR @${trans.agent} ^$senzieName"
      requestContext.complete(Contract(trans.uid, senz))
  }

  def handleResponse(response: String, connection: ActorRef): Unit = {
    // parse response and get 'TransResp'
    TransUtils.getTransResp(response) match {
      case TransResp(_, "00", _) =>
        logger.debug("Transaction done")

        // update db
        Await.result(TranDAO.updateStatus(Transaction(trans.uid, trans.customer, trans.amount, trans.timestamp, "DONE", trans.mobile, trans.agent)), 10.seconds)

        // send success status back
        val senz = s"DATA #uid${trans.uid} #status DONE @${trans.agent} ^$senzieName"
        requestContext.complete(Contract(trans.uid, senz))
      case TransResp(_, status, _) =>
        logger.error("Transaction fail with stats: " + status)

        // update db
        Await.result(TranDAO.updateStatus(Transaction(trans.uid, trans.customer, trans.amount, trans.timestamp, "ERROR", trans.mobile, trans.agent)), 10.seconds)

        // send fail status back
        val senz = s"DATA #uid${trans.uid} #status ERROR @${trans.agent} ^$senzieName"
        requestContext.complete(Contract(trans.uid, senz))
      case transResp =>
        logger.error("Invalid response " + transResp)

        // send fail status back
        val senz = s"DATA #uid${trans.uid} #status ERROR @${trans.agent} ^$senzieName"
        requestContext.complete(Contract(trans.uid, senz))
    }
  }
}


