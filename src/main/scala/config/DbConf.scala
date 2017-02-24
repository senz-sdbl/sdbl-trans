package config

import com.typesafe.config.ConfigFactory
import slick.driver.MySQLDriver.api._

import scala.util.Try

trait DbConf {
  // config object
  val config = ConfigFactory.load("database.conf")

  // cassandra db config
  lazy val cassandraHost = Try(config.getString("db.cassandra.host")).getOrElse("localhost")
  lazy val cassandraPort = Try(config.getInt("db.cassandra.port")).getOrElse(9160)

  // mysql config
  lazy val mysqlHost = Try(config.getString("db.mysql.host")).getOrElse("dev.localhost")
  lazy val mysqlPort = Try(config.getInt("db.mysql.port")).getOrElse(3306)
  lazy val mysqlUser = Try(config.getString("db.mysql.user")).getOrElse("root")
  lazy val mysqlPassword = Try(config.getString("db.mysql.password")).getOrElse("root")

  val db = Database.forURL(url = s"jdbc:mysql://$mysqlHost:$mysqlPort/sdbl", user = mysqlUser, password = mysqlPassword, driver = "com.mysql.jdbc.Driver")
}

