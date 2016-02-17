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


    def "should persist over 100 snapshots with proper sequence of primary keys"() {
        given:
        (150..1).each{
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: it))
        }

        when:
        def query = QueryBuilder.byInstanceId(1, SnapshotEntity).limit(150).build()
        def snapshots = javers.findSnapshots(query)
        def intPropertyValues = snapshots.collect { it.getPropertyValue("intProperty") }

        then:
        intPropertyValues == 1..150
    }


}
