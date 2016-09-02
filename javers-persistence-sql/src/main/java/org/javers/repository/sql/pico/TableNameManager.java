package org.javers.repository.sql.pico;

import org.javers.repository.sql.schema.FixedSchemaFactory;
import org.polyjdbc.core.PolyJDBC;

/**
 * @author Ian Agius
 */
public class TableNameManager {

    private final String SCHEMA_TABLE_SEP = ".";

    private final String schemaName;

    public TableNameManager(PolyJDBC polyJDBC) {
        schemaName = polyJDBC.schemaName();
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getGlobalIdTableNameWithSchema() {
        if (schemaName == null || schemaName.isEmpty()) {
            return FixedSchemaFactory.GLOBAL_ID_TABLE_NAME;
        } else {
            return schemaName+SCHEMA_TABLE_SEP+FixedSchemaFactory.GLOBAL_ID_TABLE_NAME;
        }
    }

    public String getGlobalIdPkSeqWithSchema() {
        if (schemaName == null || schemaName.isEmpty()) {
            return FixedSchemaFactory.GLOBAL_ID_PK_SEQ;
        } else {
            return schemaName+SCHEMA_TABLE_SEP+FixedSchemaFactory.GLOBAL_ID_PK_SEQ;
        }
    }

    public String getCommitTableNameWithSchema() {
        if (schemaName == null || schemaName.isEmpty()) {
            return FixedSchemaFactory.COMMIT_TABLE_NAME;
        } else {
            return schemaName+SCHEMA_TABLE_SEP+FixedSchemaFactory.COMMIT_TABLE_NAME;
        }
    }

    public String getCommitPkSeqWithSchema() {
        if (schemaName == null || schemaName.isEmpty()) {
            return FixedSchemaFactory.COMMIT_PK_SEQ;
        } else {
            return schemaName+SCHEMA_TABLE_SEP+FixedSchemaFactory.COMMIT_PK_SEQ;
        }
    }

    public String getCommitPropertyTableNameWithSchema() {
        if (schemaName == null || schemaName.isEmpty()) {
            return FixedSchemaFactory.COMMIT_PROPERTY_TABLE_NAME;
        } else {
            return schemaName+SCHEMA_TABLE_SEP+FixedSchemaFactory.COMMIT_PROPERTY_TABLE_NAME;
        }
    }

    public String getSnapshotTableNameWithSchema() {
        if (schemaName == null || schemaName.isEmpty()) {
            return FixedSchemaFactory.SNAPSHOT_TABLE_NAME;
        } else {
            return schemaName+SCHEMA_TABLE_SEP+FixedSchemaFactory.SNAPSHOT_TABLE_NAME;
        }
    }

    public String getSnapshotTablePkSeqWithSchema() {
        if (schemaName == null || schemaName.isEmpty()) {
            return FixedSchemaFactory.SNAPSHOT_TABLE_PK_SEQ;
        } else {
            return schemaName+SCHEMA_TABLE_SEP+FixedSchemaFactory.SNAPSHOT_TABLE_PK_SEQ;
        }
    }

    public String getCdoClassTableNameWithSchema() {
        if (schemaName == null || schemaName.isEmpty()) {
            return FixedSchemaFactory.CDO_CLASS_TABLE_NAME;
        } else {
            return schemaName+SCHEMA_TABLE_SEP+FixedSchemaFactory.CDO_CLASS_TABLE_NAME;
        }
    }

    /**
     * Sequences should be stored in the same Schema with their respective tables.
     */
    public String getSequenceNameWithSchema(String pkColName) {
        if (schemaName == null || schemaName.isEmpty()) {
            return "jv_"+pkColName+"_seq";
        } else {
            return schemaName+".jv_"+pkColName+"_seq";
        }
    }
}
