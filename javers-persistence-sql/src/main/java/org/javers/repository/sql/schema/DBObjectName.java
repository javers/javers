package org.javers.repository.sql.schema;

import java.util.Optional;

/**
 * @author bartosz.walacik
 */
public class DBObjectName {
    private final String SCHEMA_TABLE_SEP = ".";

    private final Optional<String> schemaName;
    private final String localName;

    DBObjectName(Optional<String> schemaName, String localName) {
        this.schemaName = schemaName;
        this.localName = localName;
    }

    public String localName() {
        return localName;
    }

    public String nameWithSchema() {
        return schemaName.map(n -> n + SCHEMA_TABLE_SEP + localName)
                         .orElse(localName);
    }

    @Override
    public String toString() {
        return nameWithSchema();
    }
}
