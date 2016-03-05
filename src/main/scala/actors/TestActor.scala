package actors

import akka.actor.{Actor, Kill}



class TestActor extends Actor {

  def receive = {
    case msg: String =>
      //throw new Exception("errorrrr")
      self ! Kill
  }
}
