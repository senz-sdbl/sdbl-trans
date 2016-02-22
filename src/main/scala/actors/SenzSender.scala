package actors

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import akka.actor.{Props, Actor}
import config.Configuration
import org.slf4j.LoggerFactory
import utils.SenzUtils

object SenzSender {

  case class InitSender()

  case class SenzMsg(msg: String)

  def props(socket: DatagramSocket): Props = Props(new SenzSender(socket))

}

class SenzSender(socket: DatagramSocket) extends Actor with Configuration {

  import SenzSender._

  def logger = LoggerFactory.getLogger(this.getClass)

  override def preStart = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case InitSender =>
      logger.debug("InitSender")

      // start RegHandler in here
      val regSenz = SenzUtils.getRegistrationSenz()
      context.actorOf(RegHandler.props(regSenz), "RegHandler")
    case SenzMsg(msg) =>
      logger.debug("SendMsg: " + msg)

      // TODO validate sign, encrypt the senz

      send(msg)
  }

  def send(msg: String) = {
    logger.debug("Sending SenzMsg: " + msg)

    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, InetAddress.getByName(switchHost), switchPort)
    socket.send(senzOut)
  }
}
