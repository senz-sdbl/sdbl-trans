package db.dao

import config.DbConf
import db.model.{Agent, AgentT}
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AgentDAO extends TableQuery(new AgentT(_)) with DbConf {
  def findById(id: Int): Future[Option[Agent]] = {
    db.run(this.filter(_.id === id).result).map(_.headOption)
  }

  def findByAccount(account: String): Future[Option[Agent]] = {
    db.run(this.filter(_.account === account).result).map(_.headOption)
  }

  def create(agent: Agent): Future[Int] = {
    // db.run(this returning this.map(_.autoInc) += agent)
    //db.run(this += agent)
    db.run(this += agent)
  }

  def update(agent: Agent) = {
    db.run(this.filter(_.id === agent.id).update(agent)).map(_ => ())
  }

  def deleteById(id: Int): Future[Int] = {
    db.run(this.filter(_.id === id).delete)
  }

  def deleteByAccount(account: String): Future[Int] = {
    db.run(this.filter(_.account === account).delete)
  }
}

