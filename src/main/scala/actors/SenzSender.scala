package actors

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

import akka.actor.{Actor, Props}
import config.Configuration
import org.slf4j.LoggerFactory
import utils.SenzUtils

case class InitSender()

case class SendSenz(msg: String)


/**
 * Created by eranga on 1/10/16.
 */
class SenzSender(socket: DatagramSocket) extends Actor with Configuration {

  def logger = LoggerFactory.getLogger(this.getClass)

  override def preStart = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case InitSender =>
      val regSenz = SenzUtils.getRegistrationSenz()
      context.actorOf(Props(classOf[RegistrationHandler], regSenz), "RegistrationHandler")
    case SendSenz(msg) =>
      logger.debug("Sending Senz: " + msg)

      // TODO validate sign, encrypt the senz

      sendSenz(msg)
  }

  def sendSenz(msg: String) = {
    val senzOut = new DatagramPacket(msg.getBytes, msg.getBytes.length, InetAddress.getByName(switchHost), switchPort)
    socket.send(senzOut)
  }
}
