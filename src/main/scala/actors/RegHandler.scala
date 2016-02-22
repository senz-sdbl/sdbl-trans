package actors

import akka.actor.{Actor, Props}
import config.Configuration
import handlers.SignatureVerificationFail
import org.slf4j.LoggerFactory

import scala.concurrent.duration._


object RegHandler {

  case class Reg(senzMsg: String)

  case class RegDone()

  case class RegFail()

  case class Registered()

  case class RegTimeout()

  def props(senzMsg: String): Props = Props(classOf[RegHandler], senzMsg)

}

class RegHandler(senzMsg: String) extends Actor with Configuration {

  import RegHandler._
  import context._

  val senzSender = context.actorSelection("/user/SenzSender")
  val pingSender = context.actorSelection("/user/PingSender")
  val senzReader = context.actorSelection("/user/SenzReader")

  // scheduler to run on 5 seconds
  val regCancellable = system.scheduler.schedule(0 milliseconds, 4 seconds, self, Reg(senzMsg))

  // send timeout message after 12 seconds
  val timeoutCancellable = system.scheduler.scheduleOnce(10 seconds, self, RegTimeout)

  def logger = LoggerFactory.getLogger(this.getClass)

  override def preStart = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case Reg(senz) =>
      logger.debug("Reg: " + senz)
      senzSender ! SendSenz(senz)
    case RegDone =>
      logger.debug("RegDone")
      regCancellable.cancel()
      timeoutCancellable.cancel()
      context.stop(self)
    case RegFail =>
      logger.error("RegFail")
      regCancellable.cancel()
      timeoutCancellable.cancel()
      context.stop(self)
    case Registered =>
      logger.debug("Registered")

      // cancel scheduler
      regCancellable.cancel()
      timeoutCancellable.cancel()

      // start ping sender and senz reader
      pingSender ! Ping
      senzReader ! InitReader

      // stop the actor
      context.stop(self)
    case SignatureVerificationFail =>
      logger.error("SignatureVerificationFail")

      // cancel scheduler
      regCancellable.cancel()
      timeoutCancellable.cancel()

      // stop the actor
      context.stop(self)
    case RegTimeout =>
      logger.error("RegTimeout")

      // cancel scheduler
      regCancellable.cancel()
      timeoutCancellable.cancel()

      // stop the actor
      context.stop(self)
  }

}