package supervision

import akka.actor.SupervisorStrategy.{Stop, Resume, Restart}
import akka.actor.{OneForOneStrategy, Props, Actor}
import supervision.Child.StartChild

object Parent {

  case object StartParent

}

/**
 * Created by eranga on 4/28/16.
 */
class Parent(time: String) extends Actor {

  import Parent._

  override def preStart() = {
    println("Start actor: " + context.self.path)
  }

  override def supervisorStrategy = OneForOneStrategy() {
    case _: RestartMeException =>
      println("Restart child")
      Restart
    case _: ResumeMeException =>
      println("Resume child")
      Resume
    case _: StopMeException =>
      println("Stop child")
      Stop
    case _: Exception =>
      println("Exception caught")
      Stop
  }

  override def receive = {
    case StartParent =>
      println("INIT PARENT with time " + time)

      // start child actor here
      //val child = context.actorOf(Props[Child], "child")
      val child = context.actorOf(Child.props("eranga"), name = "child")
      child ! StartChild
  }
}
