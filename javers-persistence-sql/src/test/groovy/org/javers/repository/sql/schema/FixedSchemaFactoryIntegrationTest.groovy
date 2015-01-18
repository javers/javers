package org.javers.repository.sql.schema

import org.javers.repository.sql.DialectName
import org.javers.repository.sql.SqlRepositoryBuilder
import spock.lang.Specification

/**
 * Integration Test, requires PostgreSQL
 *
 * @author bartosz walacik
 */
class FixedSchemaFactoryIntegrationTest extends Specification {


    def "should create schema"() {
        when:
        def repo = SqlRepositoryBuilder.sqlRepository().withDialect(DialectName.H2)

        //TODO ...

        then:
        true
    }
}
