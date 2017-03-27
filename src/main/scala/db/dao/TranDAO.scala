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

  def getOrCreate(trans: Transaction) = {
    val goc = (for {
      t <- this.filter(_.uid === trans.uid).result.headOption
      r <- t.map(DBIO.successful).getOrElse(this += trans)
    } yield {
      r match {
        case i: Int => if (i == 1) trans else DBIO.failed(new Exception("Faild to create transaction"))
        case t: Transaction => t
      }
    }).transactionally
    //
    //    val goc = this.filter(_.uid === trans.uid).result.headOption.flatMap {
    //      case Some(t) =>
    //        DBIO.successful(t)
    //      case None =>
    //        this += trans
    //        //DBIO.successful(trans)
    //    }.transactionally

    db.run(goc)

    //val t = this.filter(_.uid === trans.uid).result.headOption.map(_.getOrElse(trans))
    //this.insertOrUpdate(t)
  }

  def updateStatus(trans: Transaction) = {
    db.run(this.filter(_.uid === trans.uid).map(t => t.status).update(trans.status))
  }

  def deleteByUID(uid: String): Future[Int] = {
    db.run(this.filter(_.uid === uid).delete)
  }
}

