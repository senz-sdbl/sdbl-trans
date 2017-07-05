package config

import com.mchange.v2.c3p0.ComboPooledDataSource
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
  lazy val dbName = Try(config.getString("mysql.dbName")).getOrElse("sdbl")
  lazy val mysqlHost = Try(config.getString("mysql.host")).getOrElse("dev.localhost")
  lazy val mysqlPort = Try(config.getInt("mysql.port")).getOrElse(3306)
  lazy val mysqlUser = Try(config.getString("mysql.user")).getOrElse("root")
  lazy val mysqlPassword = Try(config.getString("mysql.password")).getOrElse("root")
  lazy val url = s"jdbc:mysql://$mysqlHost:$mysqlPort/$dbName"

  val db = {
    val ds = new ComboPooledDataSource
    ds.setDriverClass("com.mysql.jdbc.Driver")
    ds.setUser(mysqlUser)
    ds.setPassword(mysqlPassword)
    ds.setJdbcUrl(url)
    ds.setMaxPoolSize(20)
    ds.setTestConnectionOnCheckin(true)
    ds.setPreferredTestQuery("SELECT 1")
    ds.setIdleConnectionTestPeriod(300)
    ds.setMaxIdleTimeExcessConnections(240)

    Database.forDataSource(ds)
  }
}

