package actors

import akka.actor.ActorSystem

/**
 * Wrap actor system with trait in order to pass with
 * self typed annotation
 *
 * @author eranga bandara(erangaeb@gmail.com)
 */
trait SenzActorSystem {
  implicit val system = ActorSystem("senz")
}
