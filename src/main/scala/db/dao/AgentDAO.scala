package db.dao

import config.{AppConf, DbConf}
import db.model.{Agent, Agents}
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AgentDAO extends TableQuery(new Agents(_)) with DbConf {
  def findById(id: Int): Future[Option[Agent]] = {
    db.run(this.filter(_.id === id).result).map(_.headOption)
  }

  def findByAccount(account: String): Future[Option[Agent]] = {
    db.run(this.filter(_.account === account).result).map(_.headOption)
  }

  def create(agent: Agent): Future[Agent] = {
    db.run(this returning this.map(_.id) into ((a, id) => a.copy(id = id)) += agent)
  }

  def update(agent: Agent) = {
    db.run(this.filter(_.id === agent.id).update(agent)).map(_ => ())
  }

  def deleteById(id: Int): Future[Int] = {
    db.run(this.filter(_.id === id).delete)
  }
}

