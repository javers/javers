package org.javers.repository.jdbc;

import org.junit.Ignore;
import org.junit.Test;

import static org.javers.repository.jdbc.JdbcDiffRepositoryBuilder.jdbcDiffRepository;

/**
 * @author bartosz walacik
 */
public class JdbcDiffRepositoryIntegrationTest {

    @Test
    @Ignore
    public void buildPostgreSchema() {
        //when
        JdbcDiffRepository jdbcDiffRepository = jdbcDiffRepository()
                .configure("integration/jdbc-postgre-test.properties")
                .build();
        //
    }
}
