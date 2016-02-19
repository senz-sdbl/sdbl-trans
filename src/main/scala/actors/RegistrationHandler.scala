package actors

import _root_.handlers.SignatureVerificationFailed
import akka.actor.Actor
import config.Configuration
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

case class Reg(senz: String)

case class RegDone()

case class RegFail()

case class Registered()

/**
 * Created by eranga on 1/22/16.
 */
class RegistrationHandler(regSenz: String) extends Actor with Configuration {

  import context._

  val senzSender = context.actorSelection("/user/SenzSender")
  val pingSender = context.actorSelection("/user/PingSender")
  val senzReader = context.actorSelection("/user/SenzReader")

  // scheduler to run on 5 seconds
  val cancellable = system.scheduler.schedule(0 milliseconds, 4 seconds, self, Reg(regSenz))

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
      cancellable.cancel()
      timeoutCancellable.cancel()
      context.stop(self)
    case RegFail =>
      logger.error("RegFail")
      cancellable.cancel()
      timeoutCancellable.cancel()
      context.stop(self)
    case Registered =>
      logger.debug("Registered")

      // cancel scheduler
      cancellable.cancel()
      timeoutCancellable.cancel()

      // start ping sender and senz reader
      pingSender ! Ping
      senzReader ! InitReader

      // stop the actor
      context.stop(self)
    case SignatureVerificationFailed =>
      logger.error("SignatureVerificationFailed")

      // cancel scheduler
      cancellable.cancel()
      timeoutCancellable.cancel()

      // stop the actor
      context.stop(self)
    case RegTimeout =>
      logger.error("RegTimeout")

      // cancel scheduler
      cancellable.cancel()
      timeoutCancellable.cancel()

      // stop the actor
      context.stop(self)
  }

}