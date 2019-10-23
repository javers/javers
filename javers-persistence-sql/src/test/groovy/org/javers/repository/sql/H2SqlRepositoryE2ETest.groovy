package org.javers.repository.sql

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.JaversBuilder
import org.javers.core.model.SnapshotEntity

import java.sql.Connection
import java.sql.DriverManager

import static org.javers.repository.sql.SqlRepositoryBuilder.sqlRepository

class H2SqlRepositoryE2ETest extends JaversSqlRepositoryE2ETest {

    @Override
    Connection createConnection() {
        DriverManager.getConnection("jdbc:h2:mem:test")
    }

    @Override
    DialectName getDialect() {
        DialectName.H2
    }

    @Override
    String getSchema() {
        return null
    }

    @Override
    boolean useRandomCommitIdGenerator() {
        false
    }

    def "should fail when schema is not created"(){
        given:
        def javers = JaversBuilder.javers()
                .registerJaversRepository(sqlRepository()
                .withConnectionProvider({ DriverManager.getConnection("jdbc:h2:mem:empty-test") } as ConnectionProvider)
                .withSchemaManagementEnabled(false)
                .withDialect(getDialect())
                .build()).build()

        when:
        javers.commit("author", new SnapshotEntity(id: 1))

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.SQL_EXCEPTION
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
