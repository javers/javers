package org.javers.repository.jdbc.schema;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.SchemaManagerFactory;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;
import org.polyjdbc.core.transaction.TransactionManager;
import org.polyjdbc.core.util.TheCloser;

import javax.sql.DataSource;
/**
 * @author bartosz walacik
 */
public class JaversSchemaManager {
    private DataSource dataSource;
    private Dialect dialect;
    private Schema schema;

    public JaversSchemaManager(DataSource dataSource, Dialect dialect, FixedSchemaFactory schemaFactory) {
        this.schema = schemaFactory.getSchema();
        this.dataSource = dataSource;
        this.dialect = dialect;
    }

    public void createSchema(){
        TransactionManager manager = new DataSourceTransactionManager(dataSource);
        SchemaManagerFactory schemaManagerFactory = new SchemaManagerFactory(manager);

        SchemaManager schemaManager = null;
        try {
            schemaManager = schemaManagerFactory.createManager();
            schemaManager.create(schema);
        } finally {
            TheCloser.close(schemaManager);
        }
    }

    public void dropSchema(){

    }
}
