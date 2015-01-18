package org.javers.repository.sql.schema

import org.javers.repository.sql.ConnectionProvider
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.SqlRepositoryBuilder
import spock.lang.Specification

import java.sql.Connection

/**
 * Integration Test, requires PostgreSQL
 *
 * @author bartosz walacik
 */
class FixedSchemaFactoryIntegrationTest extends Specification {


    def "should create schema"() {
        when:
        def repo = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(new ConnectionProvider() {
                    @Override
                    Connection getConnection() {
                        return null
                    }
                })
                .withDialect(DialectName.H2).build()

        //TODO ...

        then:
        true
    }
}
