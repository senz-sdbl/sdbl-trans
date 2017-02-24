package db.dao

import config.Configuration
import db.model.{Tran, TransT}
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object TranDAO extends TableQuery(new TransT(_)) with Configuration {
  def findById(id: Int): Future[Option[Tran]] = {
    db.run(this.filter(_.id === id).result).map(_.headOption)
  }

  def create(tran: Tran): Future[Tran] = {
    db.run(this returning this.map(_.id) into ((t, id) => t.copy(id = id)) += tran)
  }

  def updateStatus(tran: Tran) = {
    db.run(this.filter(_.id === tran.id).map(t => t.status).update(tran.status))
  }

  def deleteById(id: Int): Future[Int] = {
    db.run(this.filter(_.id === id).delete)
  }
}

