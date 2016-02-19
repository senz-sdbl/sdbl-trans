package actors

import java.net.{DatagramPacket, DatagramSocket}

import akka.actor.Actor
import components.CassandraTransDbComp
import handlers.SenzHandler
import db.SenzCassandraCluster
import org.slf4j.LoggerFactory
import utils.SenzParser

case class InitListener()

/**
 * Created by eranga on 1/9/16.
 */
class SenzListener(socket: DatagramSocket) extends Actor {

  val senzHandler = new SenzHandler with CassandraTransDbComp with SenzCassandraCluster

  def logger = LoggerFactory.getLogger(this.getClass)

  override def preStart = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case InitListener => {
      val bufSize = 1024
      val buf = new Array[Byte](bufSize)

      // receiving packet
      val senzIn = new DatagramPacket(buf, bufSize)

      // listen for udp socket in order to receive messages
      while (true) {
        socket.receive(senzIn)
        val msg = new String(senzIn.getData)

        logger.debug("Senz received: " + msg)

        // handle received senz
        // parse senz first
        val senz = SenzParser.getSenz(msg)
        senzHandler.Handler.handle(senz)
      }
    }
  }
}
