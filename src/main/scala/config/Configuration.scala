package config

import com.typesafe.config.ConfigFactory

import util.Try

/**
 * Load configurations define in application.conf from here
 *
 * @author eranga herath(erangaeb@gmail.com)
 */
trait Configuration {
  // config object
  val config = ConfigFactory.load()

  // senz config
  lazy val switchName = Try(config.getString("senz.switch-name")).getOrElse("")
  lazy val clientName = Try(config.getString("senz.client-name")).getOrElse("")

  // server config
  lazy val switchHost = Try(config.getString("switch.host")).getOrElse("localhost")
  lazy val switchPort = Try(config.getInt("switch.port")).getOrElse(9999)

  // mongodb config
  lazy val mongodbHost = Try(config.getString("db.mongo.host")).getOrElse("dev.localhost")
  lazy val mongodbPort = Try(config.getInt("db.mongo.port")).getOrElse(27017)
  lazy val mongodbName = Try(config.getString("db.mongo.name")).getOrElse("senz")

  // cassandra config
  lazy val cassandraHost = Try(config.getString("db.cassandra.host")).getOrElse("localhost")
  lazy val cassandraKeyspace = Try(config.getString("db.cassandra.keyspace")).getOrElse("senz")

  // keys config
  lazy val keysDir = Try(config.getString("keys.dir")).getOrElse(".keys")
  lazy val publicKeyLocation = Try(config.getString("keys.public-key-location")).getOrElse(".keys/id_rsa.pub")
  lazy val privateKeyLocation = Try(config.getString("keys.private-key-location")).getOrElse(".keys/id_rsa")
}
