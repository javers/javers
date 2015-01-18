package org.javers.repository.sql.schema

import org.javers.core.json.JsonConverterBuilder
import org.javers.repository.sql.ConnectionProvider
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.SqlRepositoryBuilder
import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager

/**
 * Integration Test, requires PostgreSQL
 *
 * @author bartosz walacik
 */
class FixedSchemaFactoryIntegrationTest extends Specification {


    def "should create schema"() {
        when:
        Class.forName("org.postgresql.Driver");
        def connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/pawel.szymczyk", "pawel.szymczyk", "");

        def repo = SqlRepositoryBuilder
                .sqlRepository()
                .withJSONConverter(JsonConverterBuilder.jsonConverter().build())
                .withConnectionProvider(new ConnectionProvider() {
            @Override
            Connection getConnection() {
                return connection
            }
        })
                .withDialect(DialectName.H2).build()

        //TODO ...

        then:
        connection.close()
    }
}
