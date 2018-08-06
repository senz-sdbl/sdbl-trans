package config

import com.typesafe.config.ConfigFactory

import scala.util.Try

/**
  * Load configurations define in application.conf from here
  *
  * @author eranga herath(erangaeb@gmail.com)
  */
trait AppConf {
  // config object
  val config = ConfigFactory.load()

  // senzie config
  lazy val senzieMode = Try(config.getString("senzie.mode")).getOrElse("DEV")
  lazy val senzieName = Try(config.getString("senzie.name")).getOrElse("sdbltrans")
  lazy val senziePort = Try(config.getInt("senzie.port")).getOrElse(8080)

  // server config
  lazy val switchName = Try(config.getString("switch.name")).getOrElse("senzswitch")
  lazy val switchHost = Try(config.getString("switch.host")).getOrElse("dev.localhost")
  lazy val switchPort = Try(config.getInt("switch.port")).getOrElse(7070)

  // epic config
  lazy val epicHost = Try(config.getString("epic.host")).getOrElse("dev.localhost")
  lazy val epicPort = Try(config.getInt("epic.port")).getOrElse(8200)

  // keys config
  lazy val keysDir = Try(config.getString("keys.dir")).getOrElse(".keys")
  lazy val publicKeyLocation = Try(config.getString("keys.public-key-location")).getOrElse(".keys/id_rsa.pub")
  lazy val privateKeyLocation = Try(config.getString("keys.private-key-location")).getOrElse(".keys/id_rsa")
}
