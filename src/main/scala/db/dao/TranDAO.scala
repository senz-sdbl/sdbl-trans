package db.dao

import config.DbConf
import db.model.{Trans, TransT}
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TranDAO extends TableQuery(new TransT(_)) with DbConf {
  def findById(id: Int): Future[Option[Trans]] = {
    db.run(this.filter(_.id === id).result).map(_.headOption)
  }

  def create(trans: Trans): Future[Trans] = {
    db.run(this returning this.map(_.id) into ((t, id) => t.copy(id = id)) += trans)
  }

  def updateStatus(trans: Trans) = {
    db.run(this.filter(_.id === trans.id).map(t => t.status).update(trans.status))
  }

  def deleteById(id: Int): Future[Int] = {
    db.run(this.filter(_.id === id).delete)
  }
}

