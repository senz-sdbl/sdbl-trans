package config

import com.typesafe.config.ConfigFactory
import slick.driver.MySQLDriver.api._

import scala.util.Try

/**
  * Load configurations define in application.conf from here
  *
  * @author eranga herath(erangaeb@gmail.com)
  */
trait Configuration {
  // config object
  val config = ConfigFactory.load()

  // senzie config
  lazy val senzieMode = Try(config.getString("senzie.mode")).getOrElse("DEV")
  lazy val senzieName = Try(config.getString("sensie.name")).getOrElse("sdbltrans")

  // server config
  lazy val switchName = Try(config.getString("switch.name")).getOrElse("senzswitch")
  lazy val switchHost = Try(config.getString("switch.host")).getOrElse("localhost")
  lazy val switchPort = Try(config.getInt("switch.port")).getOrElse(7070)

  // epic config
  lazy val epicHost = Try(config.getString("epic.host")).getOrElse("localhost")
  lazy val epicPort = Try(config.getInt("epic.port")).getOrElse(8080)

  // keys config
  lazy val keysDir = Try(config.getString("keys.dir")).getOrElse(".keys")
  lazy val publicKeyLocation = Try(config.getString("keys.public-key-location")).getOrElse(".keys/id_rsa.pub")
  lazy val privateKeyLocation = Try(config.getString("keys.private-key-location")).getOrElse(".keys/id_rsa")

  // cassandra db config
  lazy val cassandraHost = Try(config.getString("db.cassandra.host")).getOrElse("localhost")
  lazy val cassandraPort = Try(config.getInt("db.cassandra.port")).getOrElse(9160)

  // mysql config
  //lazy val db = Database.forConfig("db.mysql")
  lazy val mysqlHost = Try(config.getString("db.cassandra.host")).getOrElse("localhost")
  lazy val mysqlPort = Try(config.getString("db.cassandra.host")).getOrElse("localhost")
  lazy val mysqlUser = Try(config.getString("db.cassandra.host")).getOrElse("localhost")
  lazy val mysqlPassword = Try(config.getString("db.cassandra.host")).getOrElse("localhost")

  val db = Database.forURL(url = "jdbc:mysql://dev.localhost:3306/sdbl", user = "root", password = "root", driver = "com.mysql.jdbc.Driver")
}
