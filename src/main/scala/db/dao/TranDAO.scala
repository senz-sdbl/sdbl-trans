package db.dao

import config.DbConf
import db.model.{Trans, TransT}
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TranDAO extends TableQuery(new TransT(_)) with DbConf {
  def findById(uid: String): Future[Option[Trans]] = {
    db.run(this.filter(_.uid === uid).result).map(_.headOption)
  }

  def create(trans: Trans): Future[Trans] = {
    db.run(this returning this.map(_.uid) into ((t, id) => t.copy(uid = id)) += trans)
  }

  def updateStatus(trans: Trans) = {
    db.run(this.filter(_.uid === trans.uid).map(t => t.status).update(trans.status))
  }

  def deleteById(uid: String): Future[Int] = {
    db.run(this.filter(_.uid === uid).delete)
  }
}

