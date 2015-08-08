package org.javers.repository.sql.integration.opendatabases

import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepositoryE2ETest

import java.sql.Connection
import java.sql.DriverManager
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class PostgreSqlIntegrationTest extends JaversSqlRepositoryE2ETest {

    Connection getConnection() {
        DriverManager.getConnection("jdbc:postgresql://localhost:5432/travis_ci_test", "postgres", "");
    }

    DialectName getDialect() {
        DialectName.POSTGRES
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
