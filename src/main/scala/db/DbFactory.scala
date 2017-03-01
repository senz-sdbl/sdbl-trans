package db

import config.DbConf
import db.model.{AgentT, TransT}
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.Future

object DbFactory extends DbConf {
//  val initDb = () => {
//    val agents = TableQuery[AgentT]
////    val trans = TableQuery[Trans]
////    val tqs = List(agents, trans)
//
//    // create db tables
//    db.run(MTable.getTables(agents.baseTableRow.tableName)).flatMap { t =>
//      if (t.isEmpty) {
//        db.run(agents.schema.create)
//      } else {
//        Future.successful()
//      }
//    }
//  }
//
//  def createTables(tables: TableQuery[_ <: Table[_]]*): Future[Seq[Unit]] = {
//    Future.sequence {
//      tables map { tq =>
//        db.run(MTable.getTables(tq.baseTableRow.tableName)).flatMap { t =>
//          if (t.isEmpty) {
//            db.run(tq.schema.create)
//          } else {
//            Future.successful()
//          }
//        }
//      }
//    }
//  }
}
