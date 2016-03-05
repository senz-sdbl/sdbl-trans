package actors

import akka.actor.{Actor, Props}
import components.CassandraTransDbComp
import crypto.RSAUtils
import db.SenzCassandraCluster
import org.slf4j.LoggerFactory
import utils.SenzUtils

object SenzReader {

  case class InitReader()

  def props(): Props = Props(new SenzReader())
}

class SenzReader extends Actor {

  import SenzReader._

  def logger = LoggerFactory.getLogger(this.getClass)

  override def preStart = {
    logger.debug("Start actor: " + context.self.path)
  }

  override def receive: Receive = {
    case InitReader => {
      // listen for user inputs form commandline
      while (true) {
        println()
        println()
        println("-----------------------------------------------")
        println("ENTER #SENZ[SHARE #acc #amnt @agent ^sdbltrans]")
        println("-----------------------------------------------")
        println()

        // read user input from the command line
        val inputSenz = scala.io.StdIn.readLine()

        if (!inputSenz.isEmpty) {
          // sign senz
          val senzSignature = RSAUtils.signSenz(inputSenz.trim.replaceAll(" ", ""))
          val signedSenz = s"$inputSenz $senzSignature"

          logger.debug("Input Senz: " + inputSenz)
          logger.debug("Signed Senz: " + signedSenz)

          // validate senz, TODO refactor
          try {
            SenzUtils.isValidSenz(signedSenz)
          } catch {
            case e: Exception =>
              logger.error("Invalid senz", e)
              println("[ERROR: INVALID SENZ]")
          }

          val shareHandlerComp = new ShareHandlerComp with CassandraTransDbComp with SenzCassandraCluster
          context.actorOf(shareHandlerComp.ShareHandler.props(signedSenz))
        } else {
          logger.error("Empty Senz")
        }
      }
    }
  }
}