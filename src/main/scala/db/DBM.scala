package db

import config.{AppConf, DbConf}
import db.dao.{AgentDAO, TranDAO}
import db.model.{Agent, AgentT, Trans, TransT}
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by eranga on 2/24/17.
  */
object DBM extends App with DbConf {
  // create tables
  val agents = TableQuery[AgentT]
  val trans = TableQuery[TransT]
  //val createSchema = agents.schema.create
  val createSchema = (agents.schema ++ trans.schema).create
  //val createSchema = trans.schema.create

  val tables = Await.result(db.run(MTable.getTables), 1.seconds).toList

  //Await.result(db.run(createSchema), 10.seconds)
  //Await.result(AgentDAO.create(Agent(1, "4232344", "TV")), 10.seconds)
  //Await.result(AgentDAO.update(Agent(2, "4232344", "FUCK")), 10.seconds)

  //Await.result(db.run(createSchema), 10.seconds)
  //Await.result(TranDAO.create(Tran(1, "44343", 30, "34442232", "P", "4232344")), 10.seconds)
  Await.result(TranDAO.updateStatus(Trans(1, "44343", 30, "34442232", "P", "4232344")), 10.seconds)
}

