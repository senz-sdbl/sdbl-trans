package supervision

/**
 * Created by eranga on 4/28/16.
 */
object SupervisorProtocol {

  case class Message(msg: String)

}

class RestartMeException extends Exception("RESTART")

class ResumeMeException extends Exception("RESUME")

class StopMeException extends Exception("STOP")

