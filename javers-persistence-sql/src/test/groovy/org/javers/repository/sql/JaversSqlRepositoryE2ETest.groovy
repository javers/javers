package org.javers.repository.sql

import org.h2.tools.Server
import org.javers.core.JaversRepositoryE2ETest
import spock.lang.Shared

import java.sql.Connection
import java.sql.DriverManager

import static org.javers.core.JaversBuilder.javers

class JaversSqlRepositoryE2ETest extends JaversRepositoryE2ETest {

    Connection dbConnection;
    
    @Override
    def setup() {
        Server.createTcpServer().start()
        dbConnection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:test")
        //dbConnection = DriverManager.getConnection("jdbc:postgresql://horton.elephantsql.com:5432/xzvpycnt", "xzvpycnt", "******");

        //PG
        //dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/javers", "javers", "javers")
        //dbConnection.setAutoCommit(false)

        def connectionProvider = new ConnectionProvider() {
            @Override
            Connection getConnection() {
               return dbConnection
            }
        }
        
        def sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(DialectName.H2).build()
        javers = javers().registerJaversRepository(sqlRepository).build()
        sqlRepository.setJsonConverter(javers.jsonConverter)
    }
    
    def cleanup() {
        dbConnection.rollback()
        dbConnection.close()
    }

    /*
    def clearTables(){
        execute("delete  from jv_snapshot_property;")
        execute("delete  from jv_snapshot;")
        execute("delete  from jv_commit;")
        execute("delete  from jv_global_id;")
        execute("delete  from jv_cdo_class;")
    }

    def execute(String sql){
        def stmt = dbConnection.createStatement()
        stmt.executeUpdate(sql)
        stmt.close()
    }*/
}
