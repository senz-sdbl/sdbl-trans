package db

import config.DbConf
import db.model.{Agents, Transactions}
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object DbFactory extends DbConf {
  val initDb = () => {
    val agents = TableQuery[Agents]
    val trans = TableQuery[Transactions]

    Await.result(createTables(agents, trans), 10.seconds)
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
