package utils

import ch.qos.logback.classic.{Level, Logger}
import config.Configuration
import crypto.RSAUtils
import org.slf4j.LoggerFactory

object SenzFactory extends Configuration {
  val setupLogging = () => {
    val rootLogger = LoggerFactory.getLogger("root").asInstanceOf[Logger]

    senzieMode match {
      case "DEV" =>
        rootLogger.setLevel(Level.DEBUG)
      case "PROD" =>
        rootLogger.setLevel(Level.INFO)
      case _ =>
        rootLogger.setLevel(Level.INFO)
    }
  }

  val setupKeys = () => {
    RSAUtils.loadRSAKeyPair()
  }
}
