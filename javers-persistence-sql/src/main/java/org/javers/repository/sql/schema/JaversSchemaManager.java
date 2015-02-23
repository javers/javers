package org.javers.repository.sql.schema;

import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.schema.*;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.util.TheCloser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author bartosz walacik
 */
public class JaversSchemaManager {
    private static final Logger logger = LoggerFactory.getLogger(JaversSchemaManager.class);

    private SchemaInspector schemaInspector;
    private SchemaManager schemaManager;
    private Dialect dialect;
    private final FixedSchemaFactory schemaFactory;

    public JaversSchemaManager(Dialect dialect, FixedSchemaFactory fixedSchemaFactory, PolyJDBC polyJDBC) {
        this.dialect = dialect;
        this.schemaFactory = fixedSchemaFactory;
        this.schemaInspector = polyJDBC.schemaInspector();
        this.schemaManager = polyJDBC.schemaManager();
    }

    public void ensureSchema() {
        for (Map.Entry<String, Schema> e : schemaFactory.allTablesSchema(dialect).entrySet()){
            ensureTable(e.getKey(), e.getValue());
        }
    }

    private void ensureTable(String tableName, Schema schema){
        try {
            if (schemaInspector.relationExists(tableName)) {
                return;
            }
            logger.info("creating javers table {} ...", tableName);
            schemaManager.create(schema);
        } finally {
            TheCloser.close(schemaManager, schemaInspector);
        }
    }

    public void dropSchema(){
        throw new RuntimeException("not implemented");
    }
}
