package actors

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Props, OneForOneStrategy, Actor}
import org.slf4j.LoggerFactory

class ParentActor extends Actor {

  def logger = LoggerFactory.getLogger(this.getClass)

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 2) {
    case _ =>
      logger.info("An actor has been killed")
      Restart
  }

  def receive = {
    case msg: String =>
      val b = context.actorOf(Props[TestActor], "T")
      b ! "DONE"
    // ceate actor b
  }
}

