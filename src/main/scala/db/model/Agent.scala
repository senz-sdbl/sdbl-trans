package db.model

import slick.driver.MySQLDriver.api._


case class Agent(account: String, branch: String)

class Agents(tag: Tag) extends Table[Agent](tag, "agents") {
  // columns
  def account = column[String]("account", O.PrimaryKey, O.Length(64))
  def branch = column[String]("branch", O.Length(64))

  // select
  def * = (account, branch) <> (Agent.tupled, Agent.unapply)
}

