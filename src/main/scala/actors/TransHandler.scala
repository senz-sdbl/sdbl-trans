package actors

import akka.actor.Actor
import org.slf4j.LoggerFactory
import protocols.Trans

import scala.concurrent.duration._

case class TransMsg(msg: String)

case class TransTimeout()

class TransHandler extends Actor {

  import context._

  def logger = LoggerFactory.getLogger(this.getClass)

  val senzSender = context.actorSelection("/user/SenzSender")
  val cancellable = system.scheduler.schedule(0 milliseconds, 4 seconds, self, TransTimeout)

  override def preStart = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case Trans(agent, timestamp, account, amount, status) =>
      logger.debug("Trans message: ")

  }
}
