package db

import config.DbConf
import db.model.{AgentT, TransT}
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object DbFactory extends DbConf {
  val initDb = () => {
    val agents = TableQuery[AgentT]
    val trans = TableQuery[TransT]

    //Await.result(createDb("SDBL"), 10.seconds)
    Await.result(createTables(agents, trans), 10.seconds)
  }

  private def createDb(dbName: String): Future[_] = {
    val db = Database.forURL(url = s"jdbc:mysql://$mysqlHost:$mysqlPort/", user = mysqlUser, password = mysqlPassword, driver = "com.mysql.jdbc.Driver")
    db.run(sqlu"CREATE DATABASE IF NOT EXISTS #$dbName")
  }

  private def createTables(tables: TableQuery[_ <: Table[_]]*): Future[Seq[Unit]] = {
    Future.sequence {
      tables map { tq =>
        db.run(MTable.getTables(tq.baseTableRow.tableName)).flatMap { t =>
          if (t.isEmpty) {
            db.run(tq.schema.create)
          } else {
            Future.successful()
          }
        }
      }
    }
  }
}