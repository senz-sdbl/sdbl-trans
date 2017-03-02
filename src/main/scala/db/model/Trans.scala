package db.model

import slick.driver.MySQLDriver.api._

case class Trans(uid: String, customer: String, amount: Int, timestamp: String, status: String, agent: String)

class TransT(tag: Tag) extends Table[Trans](tag, "TransT") {
  // columns
  def uid = column[String]("UID", O.Length(64), O.PrimaryKey)
  def customer = column[String]("CUSTOMER", O.Length(64))
  def amount = column[Int]("AMOUNT")
  def timestamp = column[String]("TIMESTAMP", O.Length(64))
  def status = column[String]("STATUS", O.Length(2))

  // foreign key
  def agent = column[String]("AGENT", O.Length(64))
  def agentFk = foreignKey("AGENT_FK", agent, TableQuery[AgentT])(_.account)

  // UID index
  def uidIdx = index("UID_IDX", uid, unique = true)

  // select
  def * = (uid, customer, amount, timestamp, status, agent) <> (Trans.tupled, Trans.unapply)
}

