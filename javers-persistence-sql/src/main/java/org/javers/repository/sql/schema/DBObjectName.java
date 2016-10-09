package org.javers.repository.sql.schema;

import org.javers.common.collections.Optional;

/**
 * @author bartosz.walacik
 */
public class DBObjectName {
    private final String SCHEMA_TABLE_SEP = ".";

    private final Optional<String> schemaName;
    private final String localName;

    public DBObjectName(Optional<String> schemaName, String localName) {
        this.schemaName = schemaName;
        this.localName = localName;
    }

    public String localName() {
        return localName;
    }

    public String nameWithSchema() {
        if (schemaName.isEmpty()) {
            return localName;
        }
        return schemaName.get() + SCHEMA_TABLE_SEP + localName;
    }
}
