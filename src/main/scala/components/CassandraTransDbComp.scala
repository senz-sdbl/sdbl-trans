package components

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder._
import db.SenzCassandraCluster
import db.model.{Agent, Transaction}

/**
  * Created by eranga on 2/2/16
  */
trait CassandraTransDbComp extends TransDbComp {

  this: SenzCassandraCluster =>

  val transDb = new CassandraTransDB

  class CassandraTransDB extends TransDb {

    def init() = {
      // query to create agent
      val sqlCreateTableAgent = "CREATE TABLE IF NOT EXISTS agent (account TEXT PRIMARY KEY, branch TEXT);"

      // queries to create trans
      val sqlCreateTableTrans = "CREATE TABLE IF NOT EXISTS trans (agent TEXT, customer TEXT, amount INT, timestamp TEXT, status TEXT, PRIMARY KEY(agent, timestamp));"
      val sqlCreateIndexTransStatus = "CREATE INDEX trans_status on trans(status);"
    }

    override def createAgent(agent: Agent) = {
      // insert query
      val statement = QueryBuilder.insertInto("agent")
        .value("account", agent.account)
        .value("branch", agent.branch)

      session.execute(statement)
    }

    override def getAgent(account: String): Option[Agent] = {
      // select query
      val selectStmt = select().all()
        .from("agent")
        .where(QueryBuilder.eq("account", account))
        .limit(1)

      val resultSet = session.execute(selectStmt)
      val row = resultSet.one()

      if (row != null) Some(Agent(row.getString("account"), row.getString("branch")))
      else None
    }

    override def createTrans(trans: Transaction) = {
      // insert query
      val statement = QueryBuilder.insertInto("trans")
        .value("agent", trans.agent)
        .value("customer", trans.customer)
        .value("amount", trans.amount)
        .value("timestamp", trans.timestamp)
        .value("status", trans.status)

      session.execute(statement)
    }

    override def updateTrans(trans: Transaction) = {
      // update query
      val statement = QueryBuilder.update("trans")
        .`with`(set("status", trans.status))
        .where(QueryBuilder.eq("timestamp", trans.timestamp)).and(QueryBuilder.eq("agent", trans.agent))

      session.execute(statement)
    }

    override def getTrans(agent: String, timestamp: String): Option[Transaction] = {
      // select query
      val selectStmt = select().all()
        .from("trans")
        .where(QueryBuilder.eq("agent", agent)).and(QueryBuilder.eq("timestamp", timestamp))
        .limit(1)

      val resultSet = session.execute(selectStmt)
      val row = resultSet.one()

      if (row != null) Some(Transaction("1", row.getString("customer"), row.getInt("amount"), row.getString("timestamp"), row.getString("status"), Option(row.getString("mobile")), row.getString("agentId")))
      else None
    }
  }

}