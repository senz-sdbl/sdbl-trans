package actors

import akka.actor.Actor
import org.slf4j.LoggerFactory
import utils.SenzUtils
import scala.concurrent.duration._

case class InitPing()

case class Ping()

/**
 * Created by eranga on 1/12/16.
 */
class PingSender extends Actor {

  import context._

  def logger = LoggerFactory.getLogger(this.getClass)

  override def preStart = {
    logger.debug("Start actor: " + context.self.path)
  }

  val senzSender = context.actorSelection("/user/SenzSender")

  override def receive: Receive = {
    case InitPing =>
      // initialize periodic ping messages
      logger.debug("INIT PING")
      self ! Ping

    case Ping =>
      logger.debug("PING")

      // send ping via sender
      val ping = SenzUtils.getPingSenz()
      senzSender ! SendSenz(ping)

      // re schedule to run on one minute
      context.system.scheduler.scheduleOnce(10 seconds, self, Ping)
  }
}