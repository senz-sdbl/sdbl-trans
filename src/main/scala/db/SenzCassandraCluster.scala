package db

import com.datastax.driver.core.{Cluster, Session}
import config.Configuration

/**
 * Cassandra database related configuration, we wrapped them with
 * trait in order to have self typed annotated dependencies
 *
 * @author eranga bandara(erangaeb@gmail.com)
 */
trait SenzCassandraCluster extends Configuration {
  lazy val cluster: Cluster = {
    Cluster.builder().
      addContactPoint(cassandraHost).
      build()
  }

  lazy val session: Session = cluster.connect(cassandraKeyspace)
}