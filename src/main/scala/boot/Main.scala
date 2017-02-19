package boot

import actors.SenzActor.InitSenz
import actors._
import akka.actor.ActorSystem
import crypto.RSAUtils
import org.slf4j.LoggerFactory

/**
  * Created by eranga on 1/9/16.
  */
object Main extends App {

  def logger = LoggerFactory.getLogger(this.getClass)

  logger.debug("Booting application")

  implicit val system = ActorSystem("senz")

  // first generate key pair if not already generated
  RSAUtils.initRSAKeys()

  // start senz actor
  val senzActor = system.actorOf(SenzActor.props, name = "SenzActor")
  senzActor ! InitSenz
}
