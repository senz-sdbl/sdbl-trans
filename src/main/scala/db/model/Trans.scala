package db.model

import slick.driver.MySQLDriver.api._

case class Trans(id: Option[Int], uid: String, customer: String, amount: Int, timestamp: String, status: String, agentId: String)

class TransT(tag: Tag) extends Table[Trans](tag, "TransT") {
  // columns
  def id = column[Option[Int]]("TRANS_ID", O.PrimaryKey, O.AutoInc)
  def uid = column[String]("TRANS_UID", O.Length(64))
  def customer = column[String]("TRANS_CUSTOMER", O.Length(64))
  def amount = column[Int]("TRANS_AMOUNT")
  def timestamp = column[String]("TRANS_TIMESTAMP", O.Length(64))
  def status = column[String]("TRANS_STATUS", O.Length(2))

  // foreign key
  def agent = column[String]("TRANS_AGENT", O.Length(64))
  def agentFk = foreignKey("AGENT_FK", agent, TableQuery[AgentT])(_.account)

  // UID index
  def uidIdx = index("TRANS_UID_IDX", uid, unique = true)

  // select
  def * = (id, uid, customer, amount, timestamp, status, agent) <> (Trans.tupled, Trans.unapply)
}

