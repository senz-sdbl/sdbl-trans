package db.model

import slick.driver.MySQLDriver.api._

case class Trans(uid: String, customer: String, amount: Int, timestamp: String, status: String, agentId: String)

class TransT(tag: Tag) extends Table[Trans](tag, "TransT") {
  // columns
  def uid = column[String]("TRANST_UID", O.PrimaryKey, O.Length(64))
  def customer = column[String]("TRANST_CUSTOMER", O.Length(64))
  def amount = column[Int]("TRANST_AMOUNT")
  def timestamp = column[String]("TRANST_TIMESTAMP", O.Length(64))
  def status = column[String]("TRANST_STATUS", O.Length(2))

  // foreign key
  def agentId = column[String]("AGENT_ID", O.Length(64))
  def agent = foreignKey("AGENT_FK", agentId, TableQuery[AgentT])(_.account)

  // select
  def * = (uid, customer, amount, timestamp, status, agentId) <> (Trans.tupled, Trans.unapply)
}

