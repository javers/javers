package org.javers.repository.sql

import org.javers.repository.jql.NewPerformanceTest
import spock.lang.Ignore

import java.sql.Connection
import java.sql.DriverManager
import static org.javers.core.JaversBuilder.javers

@Ignore
class NewSqlPerformanceTest extends NewPerformanceTest {

    Connection dbConnection

    def setup() {
        dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/javers", "javers", "javers")

        //dbConnection = DriverManager.getConnection("jdbc:mysql://192.168.99.100:32774/javers_db", "javers", "javers");
        //dbConnection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.99.100:49161:xe", "javers", "javers");
        //dbConnection = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;DatabaseName=polly", "polly", "polly");

        dbConnection.setAutoCommit(false)

        def connectionProvider = { dbConnection } as ConnectionProvider

        def sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(DialectName.POSTGRES).build()
        javers = javers().registerJaversRepository(sqlRepository).build()
    }

    @Override
    void clearDatabase(){
        execute("delete  from jv_commit_property")
        execute("delete  from jv_snapshot")
        execute("delete  from jv_commit")
        execute("delete  from jv_global_id")
    }

    @Override
    void commitDatabase() {
        dbConnection.commit()
    }

    void execute(String sql){
        println (sql)
        def stmt = dbConnection.createStatement()
        stmt.executeUpdate(sql)
        stmt.close()
    }
}
