package org.javers.repository.jdbc.schema;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;
import javax.sql.DataSource;

/**
 * @author bartosz walacik
 */
@Deprecated
public class JaversSchemaManagerIntegrationTest {
    private JaversSchemaManager javersSchemaManager;

    @Before
    public void before() {
        Dialect dialect = DialectRegistry.H2.getDialect();
        //DataSource dataSource = DataSourceFactory.create(dialect, "jdbc:h2:mem:test", "", "");

        //Dialect dialect = DialectRegistry.POSTGRES.getDialect();
        //DataSource dataSource = DataSourceFactory.create(dialect,"jdbc:postgresql://localhost/javers_devel","javers","p_javers");

        //javersSchemaManager = new JaversSchemaManager(dataSource, null, new FixedSchemaFactory());
    }
    @Test
    @Ignore
    public void testSchemaCreate() {
        javersSchemaManager.createSchema();
    }
}
