package actors

import actors.SenzSender.SendSenz
import akka.actor.{Actor, Props}
import org.slf4j.LoggerFactory
import utils.SenzUtils

import scala.concurrent.duration._


object PingSender {

  case class InitPing()

  case class Ping()

  def props(): Props = Props(new PingSender())

}

class PingSender extends Actor {

  import PingSender._
  import context._

  val senzSender = context.actorSelection("/user/SenzSender")

  def logger = LoggerFactory.getLogger(this.getClass)

  override def preStart = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case InitPing =>
      logger.debug("InitPing")

      // start scheduler to PING on every 10 seconds
      system.scheduler.schedule(0 milliseconds, 10 minutes, self, Ping)

    case Ping =>
      logger.debug("PING")

      // send ping via sender
      val ping = SenzUtils.getPingSenz()
      senzSender ! SendSenz(ping)
  }
}