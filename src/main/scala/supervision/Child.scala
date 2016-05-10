package supervision

import akka.actor.{Props, Actor}

object Child {

  case object StartChild

  def props(senzMsg: String): Props = Props(new Child())

}

/**
 * Created by eranga on 4/28/16.
 */
class Child extends Actor {

  import Child._

  override def preStart() = {
    println("Start actor: " + context.self.path)
  }

  override def receive = {
    case StartChild =>
      println("INIT CHILD")
      throw new RestartMeException
      println("POST exception")
  }
}
