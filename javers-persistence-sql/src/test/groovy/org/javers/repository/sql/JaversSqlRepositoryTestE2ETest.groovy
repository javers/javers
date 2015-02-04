package org.javers.repository.sql

import org.h2.jdbcx.JdbcConnectionPool
import org.h2.tools.Server
import org.javers.core.JaversRepositoryE2ETest
import spock.lang.Ignore

import java.sql.Connection
import java.sql.DriverManager

import static org.javers.core.JaversBuilder.javers

class JaversSqlRepositoryTestE2ETest extends JaversRepositoryE2ETest {

    Connection dbConnection;
    
    @Override
    def setup() {
        Server.createTcpServer().start()
        dbConnection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:test")
//        dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/pawel.szymczyk", "pawel.szymczyk", "");
        def connectionProvider = new ConnectionProvider() {
            @Override
            Connection getConnection() {
               return dbConnection
            }
        }
        
        def sqlRepository = SqlRepositoryBuilder.sqlRepository().withConnectionProvider(connectionProvider).withDialect(DialectName.H2).build()
        javers = javers().registerJaversRepository(sqlRepository).build()
        sqlRepository.setJsonConverter(javers.jsonConverter)
    }
    
    def cleanup() {
//        dbConnection.commit()
    }
}
