package org.javers.repository.sql

import org.h2.tools.Server
import org.javers.core.JaversRepositoryE2ETest
import org.javers.core.model.SnapshotEntity
import org.javers.repository.sql.reposiotries.PersistentGlobalId
import spock.lang.Shared

import java.sql.Connection
import java.sql.DriverManager

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId

class JaversSqlRepositoryE2ETest extends JaversRepositoryE2ETest {

    Connection dbConnection;
    
    @Override
    def setup() {
        Server.createTcpServer().start()
        dbConnection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:test");//;TRACE_LEVEL_SYSTEM_OUT=2")
        //dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/javers", "javers", "javers")

        dbConnection.setAutoCommit(false)

        def connectionProvider = { dbConnection } as ConnectionProvider
        
        def sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(DialectName.H2).build()
        javers = javers().registerJaversRepository(sqlRepository).build()
    }

    def "should not interfere with user transactions"() {
        given:
        def anEntity = new SnapshotEntity(id:1)

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
        dbConnection.close()
    }
}
