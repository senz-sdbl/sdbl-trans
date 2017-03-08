package db.model

import slick.driver.MySQLDriver.api._

case class Transaction(uid: String, customer: String, amount: Int, timestamp: String, status: String, mobile: Option[String], agent: String)

class Transactions(tag: Tag) extends Table[Transaction](tag, "Transactions") {
  // columns
  def uid = column[String]("uid", O.Length(64), O.PrimaryKey)
  def customer = column[String]("customer", O.Length(64))
  def amount = column[Int]("amount")
  def timestamp = column[String]("timestamp", O.Length(64))
  def status = column[String]("status", O.Length(2))
  def mobile = column[Option[String]]("mobile", O.Length(64))

  // foreign key
  def agent = column[String]("agent", O.Length(64))
  def agentFk = foreignKey("agent_fk", agent, TableQuery[Agents])(_.account)

  // select
  def * = (uid, customer, amount, timestamp, status, mobile, agent) <> (Transaction.tupled, Transaction.unapply)
}

