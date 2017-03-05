package config

import com.typesafe.config.ConfigFactory
import slick.driver.MySQLDriver.api._

import scala.util.Try

trait DbConf {
  // config object
  val config = ConfigFactory.load("database.conf")

  // cassandra db config
  lazy val cassandraHost = Try(config.getString("cassandra.host")).getOrElse("localhost")
  lazy val cassandraPort = Try(config.getInt("cassandra.port")).getOrElse(9160)

  // mysql config
  lazy val mysqlHost = Try(config.getString("mysql.host")).getOrElse("dev.localhost")
  lazy val mysqlPort = Try(config.getInt("mysql.port")).getOrElse(3306)
  lazy val mysqlUser = Try(config.getString("mysql.user")).getOrElse("root")
  lazy val mysqlPassword = Try(config.getString("mysql.password")).getOrElse("root")

  val db = Database.forURL(url = s"jdbc:mysql://$mysqlHost:$mysqlPort/SDBL", user = mysqlUser, password = mysqlPassword, driver = "com.mysql.jdbc.Driver")
}

