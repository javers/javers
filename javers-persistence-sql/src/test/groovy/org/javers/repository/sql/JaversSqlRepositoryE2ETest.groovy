package org.javers.repository.sql

import org.h2.tools.Server
import org.javers.core.JaversRepositoryE2ETest
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import org.javers.repository.sql.reposiotries.PersistentGlobalId

import java.sql.Connection
import java.sql.DriverManager

import static org.javers.core.JaversBuilder.javers

class JaversSqlRepositoryE2ETest extends JaversRepositoryE2ETest {

    protected Connection getConnection() {
        DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:test;")//TRACE_LEVEL_SYSTEM_OUT=2")
    }

    protected DialectName getDialect() {
        DialectName.H2
    }

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
                .withDialect(getDialect()).build()
        javers = javers().registerJaversRepository(sqlRepository).build()
        clearTables()

        dbConnection.commit()
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
        def anEntity = new SnapshotEntity(id:1)

        when:
        javers.commit("author", anEntity)
        dbConnection.rollback()
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())

        then:
        !snapshots

        when:
        javers.commit("author", anEntity)
        dbConnection.commit()
        snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())

        then:
        snapshots.size() == 1
    }

    def "should preserve globalId.pk as PersistentGlobalId to minimize number of queries"() {
        given:
        def anEntity = new SnapshotEntity(id:1)
        javers.commit("author", anEntity)

        when:
        anEntity.intProperty = 2
        def commit = javers.commit("author", anEntity)

        then:
        commit.snapshots.get(0).globalId instanceof PersistentGlobalId
        commit.snapshots.get(0).globalId.primaryKey > 0
    }

    def cleanup() {
        dbConnection.rollback()
        dbConnection.close()
    }
}
