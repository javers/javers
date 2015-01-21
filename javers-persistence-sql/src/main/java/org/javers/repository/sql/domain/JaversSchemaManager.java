package org.javers.repository.sql.domain;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.schema.*;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.transaction.TransactionManager;
import org.polyjdbc.core.util.TheCloser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author bartosz walacik
 */
public class JaversSchemaManager {
    private static final Logger logger = LoggerFactory.getLogger(JaversSchemaManager.class);

    private FixedSchemaFactory schemaFactory;
    private TransactionManager transactionManager;
    private Dialect dialect;

    public JaversSchemaManager(Dialect dialect, FixedSchemaFactory schemaFactory, TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.schemaFactory = schemaFactory;
        this.dialect = dialect;
    }

    public void ensureSchema() {
        for (Map.Entry<String, Schema> e : schemaFactory.allTablesSchema(dialect).entrySet()){
            ensureTable(e.getKey(), e.getValue());
        }
    }

    private void ensureTable(String tableName, Schema schema){
        SchemaManagerFactory schemaManagerFactory = new SchemaManagerFactory(transactionManager);
        SchemaManager schemaManager = null;
        SchemaInspector schemaInspector = null;
        try {
            schemaInspector = schemaManagerFactory.createInspector();
            if (schemaInspector.relationExists(tableName)) {
                return;
            }
            logger.info("creating javers table {} ...", tableName);
            schemaManager = schemaManagerFactory.createManager();
            schemaManager.create(schema);
        } finally {
            TheCloser.close(schemaManager, schemaInspector);
        }
    }

    public void dropSchema(){
        throw new RuntimeException("not implemented");
    }
}
