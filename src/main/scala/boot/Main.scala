package boot

import java.net.DatagramSocket

import actors._
import akka.actor.{ActorSystem, Props}
import crypto.RSAUtils
import org.slf4j.LoggerFactory

/**
 * Created by eranga on 1/9/16.
 */
object Main extends App {

  def logger = LoggerFactory.getLogger(this.getClass)

  logger.debug("Booting application")

  implicit val system = ActorSystem("senz")

  // this is the datagram socket that uses to connect to senz switch
  val socket = new DatagramSocket()

  // first generate key pair if not already generated
  RSAUtils.initRSAKeys()

  // start senz sender
  val senzSender = system.actorOf(Props(classOf[SenzSender], socket), name = "SenzSender")
  senzSender ! InitSender

  // start senz listener
  val senzListener = system.actorOf(Props(classOf[SenzListener], socket), name = "SenzListener")
  senzListener ! InitListener

  // create ping sender and senz reader
  // we will start them after registration
  val senzReader = system.actorOf(Props[SenzReader], name = "SenzReader")
  val pingSender = system.actorOf(Props[PingSender], name = "PingSender")
}
