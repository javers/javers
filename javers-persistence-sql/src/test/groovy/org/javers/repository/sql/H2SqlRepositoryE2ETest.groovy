package org.javers.repository.sql

import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import java.sql.Connection
import java.sql.DriverManager

class H2SqlRepositoryE2ETest extends JaversSqlRepositoryE2ETest {

    Connection createConnection() {
        DriverManager.getConnection( "jdbc:h2:mem:test" )
    }

    DialectName getDialect() {
        DialectName.H2
    }

    String getSchema() {
        return null
    }
}
