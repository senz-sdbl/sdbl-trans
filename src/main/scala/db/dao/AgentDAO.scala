package db.dao

import config.DbConf
import db.model.{Agent, Agents}
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AgentDAO extends TableQuery(new Agents(_)) with DbConf {
  def findByAccount(account: String): Future[Option[Agent]] = {
    db.run(this.filter(_.account === account).result).map(_.headOption)
  }

  def create(agent: Agent): Future[Int] = {
    db.run(this += agent)
  }

  def update(agent: Agent) = {
    db.run(this.filter(_.account === agent.account).update(agent)).map(_ => ())
  }

  def deleteByAccount(account: String): Future[Int] = {
    db.run(this.filter(_.account === account).delete)
  }
}

