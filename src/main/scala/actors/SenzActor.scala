package actors

import java.net.{InetAddress, InetSocketAddress}

import akka.actor.{Actor, ActorRef, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import config.AppConf
import crypto.RSAUtils
import org.slf4j.LoggerFactory
import protocols.{Msg, Senz, SenzType}
import utils.{SenzParser, SenzUtils, TransUtils}

object SenzActor {

  case class InitSenz()

  def props: Props = Props(new SenzActor)

}

class SenzActor extends Actor with AppConf {

  import context._

  def logger = LoggerFactory.getLogger(this.getClass)

  // connect to senz tcp
  val remoteAddress = new InetSocketAddress(InetAddress.getByName(switchHost), switchPort)
  IO(Tcp) ! Connect(remoteAddress)

  override def preStart() = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case c@Connected(remote, local) =>
      logger.debug("TCP connected")

      // tcp conn
      val connection = sender()
      connection ! Register(self)

      // send reg message
      val regSenzMsg = SenzUtils.getRegistrationSenzMsg
      val senzSignature = RSAUtils.signSenz(regSenzMsg.trim.replaceAll(" ", ""))
      val signedSenz = s"$regSenzMsg $senzSignature"

      logger.info("Signed senz: " + signedSenz)

      connection ! Write(ByteString(s"$signedSenz;"))

      // wait register
      context.become(registering(connection))
    case CommandFailed(_: Connect) =>
      // failed to connect
      logger.error("CommandFailed[Failed to connect]")
  }

  def registering(connection: ActorRef): Receive = {
    case CommandFailed(w: Write) =>
      logger.error("CommandFailed[Failed to write]")
    case Received(data) =>
      val senzMsg = data.decodeString("UTF-8")
      logger.debug("Received senzMsg : " + senzMsg)

      if (!senzMsg.equalsIgnoreCase("TIK;")) {
        // wait for REG status
        // parse senz first
        val senz = SenzParser.parseSenz(senzMsg)
        senz match {
          case Senz(SenzType.DATA, `switchName`, receiver, attr, signature) =>
            attr.get("#status") match {
              case Some("REG_DONE") =>
                logger.info("Registration done")

                // senz listening
                context.become(listening(connection))
              case Some("REG_ALR") =>
                logger.info("Already registered, continue system")

                // senz listening
                context.become(listening(connection))
              case Some("REG_FAIL") =>
                logger.error("Registration fail, stop system")
                context.stop(self)
              case other =>
                logger.error("UNSUPPORTED DATA message " + other)
            }
          case any =>
            logger.debug(s"Not support other messages $senzMsg this stats")
        }
      }
  }

  def listening(connection: ActorRef): Receive = {
    case CommandFailed(w: Write) =>
      logger.error("CommandFailed[Failed to write]")
    case Received(data) =>
      val senzMsg = data.decodeString("UTF-8")
      logger.debug("Received senzMsg : " + senzMsg)

      if (!senzMsg.equalsIgnoreCase("TIK;")) {
        // only handle trans here
        // parse senz first
        val senz = SenzParser.parseSenz(senzMsg)
        senz match {
          case Senz(SenzType.PUT, sender, receiver, attr, signature) =>
            // handle transaction request via trans actor
            val trans = TransUtils.getTrans(senz)
            context.actorOf(TransHandler.props(trans))
          case any =>
            logger.debug(s"Not support other messages $senzMsg this stats")
        }
      }
    case _: ConnectionClosed =>
      logger.debug("ConnectionClosed")
      context.stop(self)
    case Msg(msg) =>
      // sign senz
      val senzSignature = RSAUtils.signSenz(msg.trim.replaceAll(" ", ""))
      val signedSenz = s"$msg $senzSignature"

      logger.info("Senz: " + msg)
      logger.info("Signed senz: " + signedSenz)

      connection ! Write(ByteString(s"$signedSenz;"))
  }

}
