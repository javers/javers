package org.javers.repository.sql

import org.h2.tools.Server
import org.javers.core.Javers
import spock.lang.Ignore
import spock.lang.Specification

import java.math.RoundingMode
import java.sql.Connection
import java.sql.DriverManager

import static org.javers.core.JaversBuilder.javers

/**
 * @author bartosz walacik
 */
@Ignore
class SqlPerformanceTest extends Specification{

    Connection dbConnection;
    Javers javers;

    def setup() {
        Server.createTcpServer().start()
        //dbConnection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:test;TRACE_LEVEL_SYSTEM_OUT=2")
        dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/javers", "javers", "javers")
        //dbConnection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "polly", "polly");
        //dbConnection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=polly", "polly", "polly");

        dbConnection.setAutoCommit(false)

        def connectionProvider = { dbConnection } as ConnectionProvider

        def sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(DialectName.POSTGRES).build()
        javers = javers().registerJaversRepository(sqlRepository).build()

        clearTables()
    }

    def "should write fast enough - insert test"() {
        def start = System.currentTimeMillis()
        def n = 100

        when:
        n.times {
            def root = produce(it*100, 9)
            javers.commit("author",root)
            dbConnection.commit()
        }
        stop(start, n)

        then:
        true
    }

    def "should write fast enough - update test"() {
        def start = System.currentTimeMillis()
        def n = 100
        def root = produce(1, 9)

        when:
        n.times {
            root.change()
            javers.commit("author",root)
            dbConnection.commit()
        }
        stop(start, n)

        then:
        true
    }


    def stop(long start, int times){
        def stop = System.currentTimeMillis()

        def opAvg = (stop-start)/times

        println "total time: "+ round(stop-start)+" ms"
        println "op avg:     "+ round(opAvg)+" ms"
    }

    String round(def what){
        new BigDecimal(what).setScale(2,RoundingMode.HALF_UP).toString()
    }

    def produce(int startingId, int n){
       def root = new PerformanceEntity(id:startingId)

       def range = startingId+1..startingId+n
       def children = range.collect{
            new PerformanceEntity(id: it)
       }

       root.refs = children
       root
    }

    def clearTables(){
        execute("delete  from jv_snapshot")
        execute("delete  from jv_commit")
        execute("delete  from jv_global_id")
    }

    def execute(String sql){
        def stmt = dbConnection.createStatement()
        stmt.executeUpdate(sql)
        stmt.close()
    }

}
