package utils

import ch.qos.logback.classic.{Level, Logger}
import config.AppConf
import crypto.RSAUtils
import org.slf4j.LoggerFactory

object SenzFactory extends AppConf {
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
    RSAUtils.initRSAKeys()
  }
}
