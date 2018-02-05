package org.javers.repository.sql;

import org.javers.repository.sql.schema.FixedSchemaFactory;
import org.javers.repository.sql.schema.JaversSchemaManager;
import org.javers.repository.sql.schema.SchemaNameAware;
import org.javers.repository.sql.schema.TableNameProvider;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.dialect.Dialect;

import javax.swing.text.html.Option;
import java.util.Optional;

/**
 * Created by antonio on 12/02/2018.
 */
public class EmptySchemaManager extends JaversSchemaManager {

    public EmptySchemaManager(Dialect dialect, FixedSchemaFactory schemaFactory, PolyJDBC polyJDBC, ConnectionProvider connectionProvider, TableNameProvider tableNameProvider) {
        super(dialect, schemaFactory, polyJDBC, connectionProvider, tableNameProvider);
    }

    public void ensureSchema() {}
}
