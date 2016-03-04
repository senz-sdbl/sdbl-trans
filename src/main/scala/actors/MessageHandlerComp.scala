package actors

import actors.SenzSender.SenzMsg
import akka.actor.{Actor, Props}
import components.MsgDbComp
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

/**
 * Actor as layered component,
 * this component contains db dependency
 *
 * @author eranga bandara(erangaeb@gmail.com)
 */
trait MessageHandlerComp {

  // cake pattern dependency
  this: MsgDbComp =>

  /**
   * Companion object of ShareHandler actor
   */
  object MessageHandler {

    case class Share(msg: String)

    case class ShareDone()

    case class ShareFail()

    case class ShareTimeout()

    def props(msg: String): Props = Props(new MessageHandler(msg))

  }

  /**
   * Share handler actor
   * @param msg share message
   */
  class MessageHandler(msg: String) extends Actor {

    import context._

    def logger = LoggerFactory.getLogger(this.getClass)

    val senzSender = context.actorSelection("/user/SenzSender")

    // send Share(msg) to actor in every 4 seconds
    val shareCancellable = system.scheduler.schedule(0 milliseconds, 4 seconds, self, Share(msg))

    // send timeout message after 12 seconds
    val timeoutCancellable = system.scheduler.scheduleOnce(10 seconds, self, ShareTimeout)

    override def preStart = {
      logger.debug("Start actor: " + context.self.path)
    }

    override def receive: Receive = {
      case Share(msg) =>
        logger.debug("SHARE: " + msg)

        senzSender ! SenzMsg(msg)

        // save msg in database
        msgDb.saveMsg(msg)
      case ShareDone =>
        // success
        logger.debug("ShareDone")

        // cancel timers
        shareCancellable.cancel()
        timeoutCancellable.cancel()

        context.stop(self)
      case ShareFail =>
        // fail
        logger.error("ShareFail")

        shareCancellable.cancel()
        timeoutCancellable.cancel()
        context.stop(self)
      case ShareTimeout =>
        logger.error("Timeout")

        // cancel scheduler
        shareCancellable.cancel()
        timeoutCancellable.cancel()

        // stop the actor
        context.stop(self)
    }
  }

}
