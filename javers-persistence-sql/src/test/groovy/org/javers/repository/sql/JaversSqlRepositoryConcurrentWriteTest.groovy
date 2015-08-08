package org.javers.repository.sql

import org.javers.core.Javers
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

import java.sql.DriverManager
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import static org.javers.core.JaversBuilder.javers

/**
 * @author bartosz.walacik
 */
class JaversSqlRepositoryConcurrentWriteTest extends Specification{

    Javers javers

    def setup(){
        def connection = DriverManager.getConnection("jdbc:h2:mem:")

        def sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider( { connection } as ConnectionProvider )
                .withDialect(DialectName.H2).build()
        javers = javers().registerJaversRepository(sqlRepository).build()
    }

    //giving up creating concurrent write test for all databases
    def "should allow concurrent writes"(){
        given:
        def executor = Executors.newFixedThreadPool(20)
        def futures = new ArrayList()
        def cnt = new AtomicInteger()
        def sId = 222
        def threads = 20
        //initial commit
        javers.commit("author", new SnapshotEntity(id: sId, intProperty: cnt.incrementAndGet()))

        when:
        (1..threads).each{
            futures << executor.submit({
                try {
                    javers.commit("author", new SnapshotEntity(id: sId, intProperty: cnt.incrementAndGet()))
                } catch (Exception e){
                    println "Exception: "+ e
                }
            } as Callable)
        }

        while( futures.count { it.done } < threads){
            println "waiting for all threads, " + futures.count { it.done } + " threads have finished ..."
            Thread.currentThread().sleep(10)
        }
        println futures.count { it.done } + " threads have finished ..."

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId(sId, SnapshotEntity).build()).size() == threads + 1
    }
}
