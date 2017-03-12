package db.model

import slick.driver.MySQLDriver.api._

case class Transaction(uid: String, customer: String, amount: Int, timestamp: String, status: String, mobile: Option[String], agent: String)

class Transactions(tag: Tag) extends Table[Transaction](tag, "transactions") {
  // columns
  def uid = column[String]("uid", O.PrimaryKey, O.Length(64))
  def customer = column[String]("customer")
  def amount = column[Int]("amount")
  def timestamp = column[String]("timestamp")
  def status = column[String]("status")
  def mobile = column[Option[String]]("mobile")

  // foreign key
  def agent = column[String]("agent", O.Length(64))
  def agentFk = foreignKey("agent_fk", agent, TableQuery[Agents])(_.account)

  // select
  def * = (uid, customer, amount, timestamp, status, mobile, agent) <> (Transaction.tupled, Transaction.unapply)
}

