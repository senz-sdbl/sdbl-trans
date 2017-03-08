package db.dao

import config.DbConf
import db.model.{Transaction, Transactions}
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TranDAO extends TableQuery(new Transactions(_)) with DbConf {
  def findByUID(uid: String): Future[Option[Transaction]] = {
    db.run(this.filter(_.uid === uid).result).map(_.headOption)
  }

  def create(trans: Transaction): Future[Int] = {
    db.run(this += trans)
  }

  def updateStatus(trans: Transaction) = {
    db.run(this.filter(_.uid === trans.uid).map(t => t.status).update(trans.status))
  }

  def deleteByUID(uid: String): Future[Int] = {
    db.run(this.filter(_.uid === uid).delete)
  }
}

