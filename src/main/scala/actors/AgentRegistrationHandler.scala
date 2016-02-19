package actors

import _root_.handlers.SignatureVerificationFailed
import akka.actor.Actor
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

case class Message(senz: String)

case class RegistrationDone()

case class RegistrationFail()

case class RegTimeout()


/**
 * Created by eranga on 1/12/16.
 */
class AgentRegistrationHandler(regSenz: String) extends Actor {

  import context._

  def logger = LoggerFactory.getLogger(this.getClass)

  val senzSender = context.actorSelection("/user/SenzSender")
  val cancellable = system.scheduler.schedule(0 milliseconds, 4 seconds, self, Message(regSenz))

  // send timeout message after 12 seconds
  val timeoutCancellable = system.scheduler.scheduleOnce(10 seconds, self, RegTimeout)

  override def preStart = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case Message(senz) =>
      logger.debug("Message received: " + senz)

      context.setReceiveTimeout(30 milliseconds)
      senzSender ! SendSenz(senz)
    case RegistrationDone =>
      // success
      logger.debug("Registration done")

      cancellable.cancel()
      timeoutCancellable.cancel()
      context.stop(self)
    case RegistrationFail =>
      // fail
      logger.error("Registration fail")

      cancellable.cancel()
      timeoutCancellable.cancel()
      context.stop(self)
    case SignatureVerificationFailed =>
      logger.error("Signature verification fail")

      // cancel scheduler
      cancellable.cancel()
      timeoutCancellable.cancel()

      // stop the actor
      context.stop(self)
    case RegTimeout =>
      logger.error("Timeout")

      // cancel scheduler
      cancellable.cancel()
      timeoutCancellable.cancel()

      // stop the actor
      context.stop(self)
  }
}
