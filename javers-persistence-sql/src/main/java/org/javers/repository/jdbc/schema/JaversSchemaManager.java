package org.javers.repository.jdbc.schema;

import org.javers.common.validation.Validate;
import org.javers.common.validation.Validate;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.schema.*;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;
import org.polyjdbc.core.transaction.TransactionManager;
import org.polyjdbc.core.util.TheCloser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import static org.javers.common.validation.Validate.*;


import static org.javers.common.validation.Validate.*;

/**
 * @author bartosz walacik
 */
public class JaversSchemaManager {
    private static final Logger logger = LoggerFactory.getLogger(JaversSchemaManager.class);

    private Schema schema;
    private FixedSchemaFactory schemaFactory;
    private TransactionManager transactionManager;

    public JaversSchemaManager(Dialect dialect, FixedSchemaFactory schemaFactory, TransactionManager transactionManager) {
        this.schema = schemaFactory.getSchema(dialect);
        this.transactionManager = transactionManager;
        this.schemaFactory = schemaFactory;
    }

    public void createSchemaIfNotExists(){
        SchemaManagerFactory schemaManagerFactory = new SchemaManagerFactory(transactionManager);

        SchemaManager schemaManager = null;
        SchemaInspector schemaInspector = null;
        try {
            schemaInspector = schemaManagerFactory.createInspector();

            if (schemaExists(schemaInspector)) {
                logger.info("javers schema exists");
                return;
            }

            logger.info("creating javers schema ...");

            schemaManager = schemaManagerFactory.createManager();
            schemaManager.create(schema);
        } finally {
            TheCloser.close(schemaManager, schemaInspector);
        }
    }

    public boolean schemaExists(SchemaInspector schemaInspector ) {
        return schemaInspector.relationExists(schemaFactory.getDiffTableName());
    }

    public void dropSchema(){

    }
}
