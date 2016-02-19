package handlers

import actors._
import akka.actor.ActorContext
import components.TransDbComp
import org.slf4j.LoggerFactory
import utils.{Senz, SenzType}

case class SignatureVerificationFailed()

/**
 * Created by eranga on 1/14/16.
 */
class SenzHandler {
  this: TransDbComp =>

  def logger = LoggerFactory.getLogger(this.getClass)

  object Handler {

    def handle(senz: Senz)(implicit context: ActorContext) = {
      senz match {
        case Senz(SenzType.GET, sender, receiver, attr, signature) =>
          logger.debug(s"GET senz @$sender ^$receiver")

          val senz = Senz(SenzType.GET, sender, receiver, attr, signature)
          handleGet(senz)
        case Senz(SenzType.PUT, sender, receiver, attr, signature) =>
          logger.debug(s"PUT senz @$sender ^$receiver")

          val senz = Senz(SenzType.PUT, sender, receiver, attr, signature)
          handlePut(senz)
        case Senz(SenzType.SHARE, sender, receiver, attr, signature) =>
          logger.debug(s"SHARE senz @$sender ^$receiver")

          val senz = Senz(SenzType.SHARE, sender, receiver, attr, signature)
          handlerShare(senz)
        case Senz(SenzType.DATA, sender, receiver, attr, signature) =>
          logger.debug(s"DATA senz @$sender ^$receiver")

          val senz = Senz(SenzType.DATA, sender, receiver, attr, signature)
          handleData(senz)
        case Senz(SenzType.PING, _, _, _, _) =>
          logger.debug(s"PING senz: IGNORE")
      }
    }

    def handleGet(senz: Senz) = {
      // save in database

      // send balance query to epic
    }

    def handlePut(senz: Senz) = {
      // save in database

      // send transaction request to epic
    }

    def handlerShare(senz: Senz) = {
      // nothing to do with share
    }

    def handleData(senz: Senz)(implicit context: ActorContext) = {
      val regActor = context.actorSelection("/user/SenzSender/RegistrationHandler")
      val agentRegActor = context.actorSelection("/user/SenzReader/*")

      senz.attributes.get("#msg") match {
        case Some("ShareDone") =>
          agentRegActor ! RegistrationDone
        case Some("ShareFail") =>
          agentRegActor ! RegistrationFail
        case Some("REGISTRATION_DONE") =>
          regActor ! RegDone
        case Some("REGISTRATION_FAIL") =>
          regActor ! RegFail
        case Some("ALREADY_REGISTERED") =>
          regActor ! Registered
        case Some("SignatureVerificationFailed") =>
          context.actorSelection("/user/Senz*") ! SignatureVerificationFailed
        case other =>
          logger.error("UNSUPPORTED DATA message " + other)
      }
    }
  }

}
