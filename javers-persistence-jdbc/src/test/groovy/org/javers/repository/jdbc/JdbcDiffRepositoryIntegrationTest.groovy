package org.javers.repository.jdbc

import org.junit.Test
import spock.lang.Ignore;
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.repository.jdbc.JdbcDiffRepositoryBuilder.jdbcDiffRepository;

/**
 * [Integration Test] requires PostgreSQL
 *
 * @author bartosz walacik
 */
class JdbcDiffRepositoryIntegrationTest extends Specification {

    @Ignore
    def "should create Postgre schema"() {
        when:
        JdbcDiffRepository jdbcDiffRepository = jdbcDiffRepository()
                .configure("integration/jdbc-postgre-test.properties")
                .build()

        then:
        //?
        true
    }
}
