package org.javers.repository.sql.integration

import org.h2.tools.Server
import org.javers.core.JaversRepositoryE2ETest
import org.javers.repository.sql.ConnectionProvider
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.SqlRepositoryBuilder

import java.sql.Connection
import java.sql.DriverManager

import static org.javers.core.JaversBuilder.javers


class MySqlIntegrationTest extends JaversRepositoryE2ETest {
    
    Connection dbConnection;

    @Override
    def setup() {
        Server.createTcpServer().start()
        dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/travis_ci_test", "travis", "");
        def connectionProvider = new ConnectionProvider() {
            @Override
            Connection getConnection() {
                return dbConnection
            }
        }

        def sqlRepository = SqlRepositoryBuilder.sqlRepository().withConnectionProvider(connectionProvider).withDialect(DialectName.MYSQL).build()
        javers = javers().registerJaversRepository(sqlRepository).build()
        sqlRepository.setJsonConverter(javers.jsonConverter)
    }

    def cleanup() {
        dbConnection.close()
    }
}
