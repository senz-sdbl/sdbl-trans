package components

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder._
import db.SenzCassandraCluster

/**
 * Created by eranga on 2/2/16
 */
trait CassandraTransDbComp extends TransDbComp {

  this: SenzCassandraCluster =>

  val transDb = new CassandraTransDB

  class CassandraTransDB extends TransDb {

    def init() = {
      val sqlCreateTableAgent = "CREATE TABLE IF NOT EXISTS agent username TEXT PRIMARY KEY, branch TEXT;"

      val sqlCreateTableBalance = "CREATE TABLE IF NOT EXISTS balance agent_id TEXT, timestamp TEXT, name TEXT, account TEXT, nic TEXT, amount TEXT, status TEXT, PRIMARY KEY(agent_id, timestamp);"
      val sqlCreateIndexBalanceStatus = "CREATE INDEX balance_status on balance(status);"

      val sqlCreateTableTransaction = "CREATE TABLE IF NOT EXISTS transaction agent_id TEXT, timestamp TEXT, account TEXT, amount TEXT, status TEXT, PRIMARY KEY(agent_id, timestamp);"
      val sqlCreateIndexTransactionStatus = "CREATE INDEX transaction_status on transaction(status);"
    }

    override def createAgent(agent: Agent) = {
      // insert query
      val statement = QueryBuilder.insertInto("agent")
        .value("name", agent.username)
        .value("branch", agent.branch)

      session.execute(statement)
    }

    override def getAgent(username: String): Agent = {
      // select query
      val selectStmt = select().all()
        .from("agent")
        .where(QueryBuilder.eq("name", username))
        .limit(1)

      val resultSet = session.execute(selectStmt)
      val row = resultSet.one()

      Agent(row.getString("name"), row.getString("branch"))
    }

    override def createBalance(balance: Balance) = {
      // insert query
      val statement = QueryBuilder.insertInto("balance")
        .value("agent", balance.agent)
        .value("branch", balance.account)
        .value("nic", balance.nic)
        .value("timestamp", balance.timestamp)
        .value("status", balance.status)

      session.execute(statement)
    }

    override def updateBalance(balance: Balance) = {
      // update query
      val statement = QueryBuilder.update("balance")
        .`with`(set("status", balance.status))
        .where(QueryBuilder.eq("timestamp", balance.timestamp)).and(QueryBuilder.eq("agent", balance.agent))

      session.execute(statement)
    }

    override def getBalance(agent: String, timestamp: String): Balance = {
      // select query
      val selectStmt = select().all()
        .from("balance")
        .where(QueryBuilder.eq("agent_id", "1")).and(QueryBuilder.eq("timestamp", "w234234"))
        .limit(1)

      val resultSet = session.execute(selectStmt)
      val row = resultSet.one()

      Balance(row.getString("agent_id"), row.getString("timestamp"), row.getString("account"), row.getString("nic"), row.getString("amount"), row.getString("status"))
    }
  }

}
