package db.model

import slick.driver.MySQLDriver.api._


case class Agent(id: Int, account: String, branch: String)

class AgentT(tag: Tag) extends Table[Agent](tag, "AgentT") {
  // columns
  def id = column[Int]("ID")
  def account = column[String]("ACCOUNT", O.PrimaryKey, O.Length(64))
  def branch = column[String]("BRANCH", O.Length(64))

  // select
  def * = (id, account, branch) <> (Agent.tupled, Agent.unapply)
}

