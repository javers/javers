package org.javers.repository.sql

import org.javers.core.Javers
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.atomic.AtomicInteger

import static groovyx.gpars.GParsPool.withPool
import static org.javers.core.JaversBuilder.javers

/**
 * @author bartosz.walacik
 */
class JaversSqlRepositoryConcurrentWriteTest extends Specification{

    Javers javers
    Connection connection

    def setup(){
        connection = DriverManager.getConnection("jdbc:h2:mem:db1")

        def sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider( { connection } as ConnectionProvider )
                .withDialect(DialectName.H2).build()
        javers = javers().registerJaversRepository(sqlRepository).build()
    }

    def cleanup(){
        connection.close()
    }

    def "should allow concurrent writes"(){
        given:
        def cnt = new AtomicInteger()
        def sId = 222
        def threads = 99
        //initial commit
        javers.commit("author", new SnapshotEntity(id: sId, intProperty: cnt.incrementAndGet()))

        when:
        withPool threads, {
            (1..threads).collectParallel {
                javers.commit("author", new SnapshotEntity(id: sId, intProperty: cnt.incrementAndGet()))
            }
        }

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId(sId, SnapshotEntity).build()).size() == threads + 1
    }
}
