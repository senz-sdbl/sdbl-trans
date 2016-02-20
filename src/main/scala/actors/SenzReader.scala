package actors

import akka.actor.Actor
import components.CassandraTransDbComp
import crypto.RSAUtils
import db.SenzCassandraCluster
import org.slf4j.LoggerFactory

case class InitReader()

/**
 * Created by eranga on 1/9/16.
 */
class SenzReader extends Actor {

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
        println("--------------------------------------------------")
        println("ENTER #SENZ[SHARE #nic #nam #acc @agent_ ^sdbltrans]")
        println("--------------------------------------------------")
        println()

        // read user input from the command line
        val inputSenz = scala.io.StdIn.readLine()

        // TODO validate senz

        if (!inputSenz.isEmpty) {
          // sign senz
          val senzSignature = RSAUtils.signSenz(inputSenz.trim.replaceAll(" ", ""))
          val signedSenz = s"$inputSenz $senzSignature"

          logger.error("Input Senz: " + inputSenz)
          logger.error("Signed Senz: " + signedSenz)

          // transaction request via trans actor
          val transHandlerComp = new TransHandlerComp with CassandraTransDbComp with SenzCassandraCluster
          context.actorOf(transHandlerComp.TransHandler.props(TransMsg("cakezzzzzz")))

          // start actor to handle the senz
          //context.actorOf(Props(classOf[AgentRegistrationHandler], signedSenz))
        } else {
          logger.error("Empty Senz")
        }
      }
    }
  }
}