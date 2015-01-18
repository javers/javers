package org.javers.repository.sql

import org.h2.jdbcx.JdbcConnectionPool
import org.h2.tools.Server
import org.javers.core.JaversRepositoryE2ETest

import java.sql.Connection

import static org.javers.core.JaversBuilder.javers


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
        def sqlRepository = new JaversSqlRepository(connectionProvider, DialectName.H2)
        javers = javers().registerJaversRepository(sqlRepository).build()
    }
}
