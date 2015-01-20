package org.javers.repository.sql.domain

import org.h2.tools.Server
import org.javers.core.json.JsonConverterBuilder
import org.javers.repository.sql.ConnectionProvider
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.SqlRepositoryTestBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager

/**
 * @author pawel szymczyk
 */
class BaseRepositoryTest extends Specification {

    @Shared def connectionProvider
    @Shared def dbConnection
    @Shared def sqlRepoBuilder
    
    def setupSpec() {
        Server.createTcpServer().start()
        dbConnection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:test")
        dbConnection.setAutoCommit(false)

        connectionProvider = new ConnectionProvider() {
            Connection getConnection() {
                return dbConnection
            }
        }

        sqlRepoBuilder = SqlRepositoryTestBuilder.sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(DialectName.H2)
                .withJSONConverter(JsonConverterBuilder.jsonConverter()
                .build())
        
        sqlRepoBuilder.build()
    }
}
