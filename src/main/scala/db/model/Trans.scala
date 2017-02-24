package db.model

import slick.driver.MySQLDriver.api._

case class Tran(id: Int, customer: String, amount: Int, timestamp: String, status: String, agentId: String)

class TransT(tag: Tag) extends Table[Tran](tag, "TransT") {
  // columns
  def id = column[Int]("TRANST_ID", O.PrimaryKey, O.AutoInc)
  def customer = column[String]("TRANST_CUSTOMER", O.Length(64))
  def amount = column[Int]("TRANST_AMOUNT")
  def timestamp = column[String]("TRANST_TIMESTAMP", O.Length(64))
  def status = column[String]("TRANST_STATUS", O.Length(2))

  // ForeignKey
  def agentId = column[String]("AGENT_ID", O.Length(64))
  def agent = foreignKey("AGENT_FK", agentId, TableQuery[Agents])(_.account)

  // select
  def * = (id, customer, amount, timestamp, status, agentId) <> (Tran.tupled, Tran.unapply)
}

