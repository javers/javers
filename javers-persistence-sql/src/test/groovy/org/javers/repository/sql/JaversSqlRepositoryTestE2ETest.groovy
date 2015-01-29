package org.javers.repository.sql

import org.h2.jdbcx.JdbcConnectionPool
import org.h2.tools.Server
import org.javers.core.JaversRepositoryE2ETest
import spock.lang.Ignore

import java.sql.Connection
import java.sql.DriverManager

import static org.javers.core.JaversBuilder.javers

class JaversSqlRepositoryTestE2ETest extends JaversRepositoryE2ETest {

    @Override
    def setup() {
        Server.createTcpServer().start()
        def dbConnection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:test")
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
}
