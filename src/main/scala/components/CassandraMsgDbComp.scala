package components

import com.datastax.driver.core.querybuilder.QueryBuilder
import db.SenzCassandraCluster

/**
 * Cassandra based MsgDbComp implementation
 */
trait CassandraTransDbComp extends MsgDbComp {

  this: SenzCassandraCluster =>

  val msgDb = new CassandraMsgDB

  class CassandraMsgDB extends MsgDb {

    override def saveMsg(msg: String) = {
      // insert query
      val statement = QueryBuilder.insertInto("Message")
        .value("msg", msg)

      session.execute(statement)
    }
  }

}
