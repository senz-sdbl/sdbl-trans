package handlers

import actors._
import akka.actor.ActorContext
import components.{CassandraTransDbComp, TransDbComp}
import db.SenzCassandraCluster
import org.slf4j.LoggerFactory
import protocols.{Senz, SenzType}
import utils.TransUtils

class SenzHandler {
  this: TransDbComp =>

  def logger = LoggerFactory.getLogger(this.getClass)

  object Handler {

    def handle(senz: Senz)(implicit context: ActorContext) = {
      senz match {
        case Senz(SenzType.GET, sender, receiver, attr, signature) =>
          logger.debug(s"GET senz: @$sender ^$receiver")

          val senz = Senz(SenzType.GET, sender, receiver, attr, signature)
          handleGet(senz)
        case Senz(SenzType.SHARE, sender, receiver, attr, signature) =>
          logger.debug(s"SHARE senz: @$sender ^$receiver")

          val senz = Senz(SenzType.SHARE, sender, receiver, attr, signature)
          handlerShare(senz)
        case Senz(SenzType.PUT, sender, receiver, attr, signature) =>
          logger.debug(s"PUT senz: @$sender ^$receiver")

          val senz = Senz(SenzType.PUT, sender, receiver, attr, signature)
          handlePut(senz)
        case Senz(SenzType.DATA, sender, receiver, attr, signature) =>
          logger.debug(s"DATA senz: @$sender ^$receiver")

          val senz = Senz(SenzType.DATA, sender, receiver, attr, signature)
          handleData(senz)
        case Senz(SenzType.PING, _, _, _, _) =>
          logger.debug(s"PING senz")
      }
    }

    def handleGet(senz: Senz)(implicit context: ActorContext) = {
      // save in database

      // send trans request to epic
    }

    def handlerShare(senz: Senz)(implicit context: ActorContext) = {
      // nothing to do with share
    }

    def handlePut(senz: Senz)(implicit context: ActorContext) = {
      // create trans form senz
      val trans = TransUtils.getTrans(senz)

      // check trans exists
      transDb.getTrans(trans.agent, trans.timestamp) match {
        case Some(existingTrans) =>
          // already existing trans
          logger.debug("Trans exists, no need to recreate: " + "[" + existingTrans.agent + ", " + existingTrans.customer + ", " + existingTrans.amount + "]")
        case None =>
          // new trans, so create and process it
          logger.debug("New Trans, process it: " + "[" + trans.agent + ", " + trans.customer + ", " + trans.amount + "]")

          // save in database
          transDb.createTrans(trans)

          // transaction request via trans actor
          val transHandlerComp = new TransHandlerComp with CassandraTransDbComp with SenzCassandraCluster
          context.actorOf(transHandlerComp.TransHandler.props(trans))
      }
    }

    def handleData(senz: Senz)(implicit context: ActorContext) = {

    }
  }

}
