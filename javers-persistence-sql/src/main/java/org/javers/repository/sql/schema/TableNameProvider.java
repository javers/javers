package org.javers.repository.sql.schema;

import java.util.Optional;
import org.javers.repository.sql.SqlRepositoryConfiguration;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author Ian Agius
 */
public class TableNameProvider {

    private final Optional<String> schemaName;

    public TableNameProvider(SqlRepositoryConfiguration configuration) {
        this.schemaName = configuration.getSchemaNameAsOptional();
    }

    public String getGlobalIdTableNameWithSchema() {
        return getGlobalIdTableName().nameWithSchema();
    }
    public String getCommitTableNameWithSchema() {
        return getCommitTableName().nameWithSchema();
    }

    public String getCommitPropertyTableNameWithSchema() {
        return getCommitPropertyTableName().nameWithSchema();
    }

    public String getSnapshotTableNameWithSchema() {
        return getSnapshotTableName().nameWithSchema();
    }

    public String getSnapshotTablePkSeqWithSchema() {
        return new DBObjectName(schemaName, SNAPSHOT_TABLE_PK_SEQ).nameWithSchema();
    }

    public String getGlobalIdPkSeqWithSchema() {
        return new DBObjectName(schemaName, GLOBAL_ID_PK_SEQ).nameWithSchema();
    }

    public String getCommitPkSeqWithSchema() {
        return new DBObjectName(schemaName, COMMIT_PK_SEQ).nameWithSchema();
    }

    /**
     * used only by migration scripts
     */
    @Deprecated
    public String getCdoClassTableNameWithSchema() {
        return new DBObjectName(schemaName, "jv_cdo_class").nameWithSchema();
    }

    public String getSequenceNameWithSchema(String pkColName) {
        return new DBObjectName(schemaName, "jv_" + pkColName + "_seq").nameWithSchema();
    }

    DBObjectName getGlobalIdTableName() {
        return new DBObjectName(schemaName, GLOBAL_ID_TABLE_NAME);
    }

    DBObjectName getCommitTableName() {
        return new DBObjectName(schemaName, COMMIT_TABLE_NAME);
    }

    DBObjectName getCommitPropertyTableName() {
        return new DBObjectName(schemaName, COMMIT_PROPERTY_TABLE_NAME);
    }

    DBObjectName getSnapshotTableName() {
        return new DBObjectName(schemaName, SNAPSHOT_TABLE_NAME);
    }
}
