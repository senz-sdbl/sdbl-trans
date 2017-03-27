package actors

import java.net.{InetAddress, InetSocketAddress}

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import config.AppConf
import db.dao.TranDAO
import db.model.Transaction
import org.slf4j.LoggerFactory
import protocols.Msg
import utils.TransUtils

import scala.concurrent.Await
import scala.concurrent.duration._

object TransHandler {

  case class InitTrans(trans: Transaction)

  case class TransMsg(msgStream: Array[Byte])

  case class TransResp(esh: String, status: String, rst: String)

  case class TransTimeout()

  def props(trans: Transaction): Props = Props(new TransHandler(trans))
}

class TransHandler(trans: Transaction) extends Actor with AppConf {

  import TransHandler._
  import context._

  def logger = LoggerFactory.getLogger(this.getClass)

  // we need senz sender to send reply back
  val senzActor = context.actorSelection("/user/SenzActor")

  // send init trans to self
  self ! InitTrans(trans)

  // handle timeout in 15 seconds
  var timeoutCancellable = system.scheduler.scheduleOnce(10 seconds, self, TransTimeout())

  override def preStart() = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case InitTrans(tr) =>
      // connect tcp
      // connect to epic tcp end
      val remoteAddress = new InetSocketAddress(InetAddress.getByName(epicHost), epicPort)
      IO(Tcp) ! Connect(remoteAddress, timeout = Option(15 seconds))

      // create transaction, if not exists
      Await.result(TranDAO.create(tr), 10.seconds)

      // send status back
      val senz = s"DATA #uid ${trans.uid} #status PENDING @${trans.agent} ^sdbltrans"
      senzActor ! Msg(senz)
    case c@Connected(remote, local) =>
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
        case CommandFailed(w: Write) =>
          logger.error("CommandFailed[Failed to write]")
        case Received(data) =>
          val response = data.decodeString("UTF-8")
          logger.debug("Received : " + response)

          // cancel timer
          timeoutCancellable.cancel()

          handleResponse(response, connection)
        case _: ConnectionClosed =>
          logger.debug("ConnectionClosed")
        case TransTimeout() =>
          // timeout
          logger.error("TransTimeout")

          // send error response
          val senz = s"DATA #uid${trans.uid} #status ERROR @${trans.agent} ^$senzieName"
          senzActor ! Msg(senz)

          // stop from here
          context.stop(self)
      }
    case CommandFailed(_: Connect) =>
      // failed to connect
      logger.error("CommandFailed[Failed to connect]")

      // send fail status back
      val senz = s"DATA #uid ${trans.uid} #status ERROR @${trans.agent} ^sdbltrans"
      senzActor ! Msg(senz)

      // stop from here
      context.stop(self)
  }

  def handleResponse(response: String, connection: ActorRef) = {
    // parse response and get 'TransResp'
    TransUtils.getTransResp(response) match {
      case TransResp(_, "00", _) =>
        logger.debug("Transaction done")

        // update db
        Await.result(TranDAO.updateStatus(Transaction(trans.uid, trans.customer, trans.amount, trans.timestamp, "D", trans.mobile, trans.agent)), 10.seconds)

        // send success status back
        val senz = s"DATA #uid${trans.uid} #status DONE @${trans.agent} ^$senzieName"
        senzActor ! Msg(senz)
      case TransResp(_, status, _) =>
        logger.error("Transaction fail with stats: " + status)

        // send fail status back
        val senz = s"DATA #uid${trans.uid} #status ERROR @${trans.agent} ^$senzieName"
        senzActor ! Msg(senz)
      case transResp =>
        logger.error("Invalid response " + transResp)

        // send fail status back
        val senz = s"DATA #uid${trans.uid} #status ERROR @${trans.agent} ^$senzieName"
        senzActor ! Msg(senz)
    }

    // stop from here
    context.stop(self)
  }
}

