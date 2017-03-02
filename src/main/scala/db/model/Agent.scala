package db.model

import slick.driver.MySQLDriver.api._

case class Agent(id: Int, account: String, branch: String)

class AgentT(tag: Tag) extends Table[Agent](tag, "AgentT") {
  // columns
  def id = column[Int]("AGENT_ID")
  def account = column[String]("AGENT_ACCOUNT", O.PrimaryKey, O.Length(64))
  def branch = column[String]("USER_BRANCH", O.Length(64))

  // indexes
  def accountIndex = index("USER_ACCOUNT_IDX", account, unique = true)

  // select
  def * = (id, account, branch) <> (Agent.tupled, Agent.unapply)
}

