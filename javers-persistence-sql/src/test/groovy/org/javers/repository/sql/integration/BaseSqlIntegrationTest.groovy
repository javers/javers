package org.javers.repository.sql.integration

import org.h2.tools.Server
import org.javers.core.JaversRepositoryE2ETest
import org.javers.core.model.SnapshotEntity
import org.javers.repository.sql.ConnectionProvider
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.SqlRepositoryBuilder
import org.javers.repository.sql.reposiotries.PersistentGlobalId

import java.sql.Connection

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId

abstract class BaseSqlIntegrationTest extends JaversRepositoryE2ETest {

    abstract Connection getConnection()

    Connection dbConnection

    @Override
    def setup() {
        Server.createTcpServer().start()
        dbConnection = getConnection()

        dbConnection.setAutoCommit(false)

        def connectionProvider = { dbConnection } as ConnectionProvider

        def sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(DialectName.H2).build()
        javers = javers().registerJaversRepository(sqlRepository).build()

        clearTables()
    }

    def clearTables() {
        execute("delete  from jv_snapshot;")
        execute("delete  from jv_commit;")
        execute("delete  from jv_global_id;")
        execute("delete  from jv_cdo_class;")
    }

    def execute(String sql) {
        def stmt = dbConnection.createStatement()
        stmt.executeUpdate(sql)
        stmt.close()
    }

    def "should not interfere with user transactions"() {
        given:
        def anEntity = new SnapshotEntity(id: 1)

        when:
        javers.commit("author", anEntity)
        dbConnection.rollback()
        def snapshots = javers.getStateHistory(instanceId(1, SnapshotEntity), 2)

        then:
        !snapshots

        when:
        javers.commit("author", anEntity)
        dbConnection.commit()
        snapshots = javers.getStateHistory(instanceId(1, SnapshotEntity), 2)

        then:
        snapshots.size() == 1
    }

    def "should preserve globalId.pk as PersistentGlobalId to minimize number of queries"() {
        given:
        def anEntity = new SnapshotEntity(id: 1)
        javers.commit("author", anEntity)

        when:
        anEntity.intProperty = 2
        def commit = javers.commit("author", anEntity)

        then:
        commit.snapshots.get(0).globalId instanceof PersistentGlobalId
        commit.snapshots.get(0).globalId.primaryKey > 0
    }

    def cleanup() {
        dbConnection.close()
    }
}
