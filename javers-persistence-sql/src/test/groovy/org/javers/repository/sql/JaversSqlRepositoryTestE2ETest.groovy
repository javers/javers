package org.javers.repository.sql

import org.h2.jdbcx.JdbcConnectionPool
import org.h2.tools.Server
import org.javers.core.JaversRepositoryE2ETest
import spock.lang.Ignore

import java.sql.Connection

import static org.javers.core.JaversBuilder.javers

@Ignore
class JaversSqlRepositoryTestE2ETest extends JaversRepositoryE2ETest {

    @Override
    def setup() {
        Server.createTcpServer().start()
        def connectionPool = JdbcConnectionPool.create("jdbc:h2:tcp://localhost/~/test", "sa", "sa")
        def connectionProvider = new ConnectionProvider() {
            @Override
            Connection getConnection() {
                connectionPool.getConnection()
            }
        }
        
        def sqlRepository = SqlRepositoryBuilder.sqlRepository().withConnectionProvider(connectionProvider).withDialect(DialectName.H2).build()
        javers = javers().registerJaversRepository(sqlRepository).build()
        sqlRepository.setJsonConverter(javers.jsonConverter)
    }
}
