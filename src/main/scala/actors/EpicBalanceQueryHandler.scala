package actors

import akka.actor.Actor

case class BalanceQuery()

/**
 * Created by eranga on 1/13/16.
 */
class EpicBalanceQueryHandler extends Actor {
  override def receive: Receive = {
    case BalanceQuery =>

  }
}
