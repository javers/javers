package org.javers.repository.jdbc.schema;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.polyjdbc.core.integration.DataSourceFactory;

import javax.sql.DataSource;

/**
 * @author bartosz walacik
 */
public class JaversSchemaManagerIntegrationTest {
    private JaversSchemaManager javersSchemaManager;

    @BeforeTest
    public void before() {
        Dialect dialect = DialectRegistry.H2.getDialect();
        DataSource dataSource = DataSourceFactory.create(dialect, "jdbc:h2:mem:test", "", "");

        //Dialect dialect = DialectRegistry.POSTGRES.getDialect();
        //DataSource dataSource = DataSourceFactory.create(dialect,"jdbc:postgresql://localhost/javers_devel","javers","p_javers");

        javersSchemaManager = new JaversSchemaManager(dataSource, null, new FixedSchemaFactory());
    }
    @Test
    public void testSchemaCreate() {
        javersSchemaManager.createSchema();
    }
}
