package org.javers.repository.sql

import org.javers.core.model.SnapshotEntity

import java.sql.Connection
import java.sql.DriverManager

class H2SqlRepositoryE2ETest extends JaversSqlRepositoryE2ETest {

    Connection createConnection() {
        DriverManager.getConnection("jdbc:h2:mem:test")
    }

    DialectName getDialect() {
        DialectName.H2
    }

    String getSchema() {
        return null
    }

    /**
     * see https://github.com/javers/javers/issues/532
     */
    def "should evict sequence allocation cache"() {
        given:
        (1..50).each {
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: it))
        }

        when:
        clearTables()
        execute("alter sequence  ${schemaPrefix()}jv_commit_pk_seq restart with 1")
        execute("alter sequence  ${schemaPrefix()}jv_global_id_pk_seq restart with 1")
        execute("alter sequence  ${schemaPrefix()}jv_snapshot_pk_seq restart with 1")
        def sqlRepository = (JaversSqlRepository) repository
        sqlRepository.evictSequenceAllocationCache()
        sqlRepository.evictCache()

        then:
        (1..150).each {
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: it))
        }
    }
}
