package org.javers.repository.sql.schema;

import java.util.Optional;
import org.javers.repository.sql.SqlRepositoryConfiguration;
import org.polyjdbc.core.PolyJDBC;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author Ian Agius
 */
public class TableNameProvider {

    private final Optional<String> schemaName;

    public TableNameProvider(SqlRepositoryConfiguration configuration) {
        this.schemaName = configuration.getSchemaNameAsOptional();
    }

    public TableNameProvider(Optional<String> schemaName){
        this.schemaName = schemaName;
    }



    public String getGlobalIdTableNameWithSchema() {
        return getGlobalIdTableName().nameWithSchema();
    }

    public DBObjectName getGlobalIdTableName() {
        return new DBObjectName(schemaName, GLOBAL_ID_TABLE_NAME);
    }

    public String getCommitTableNameWithSchema() {
        return getCommitTableName().nameWithSchema();
    }

    public DBObjectName getCommitTableName() {
        return new DBObjectName(schemaName, COMMIT_TABLE_NAME);
    }

    public String getCommitPropertyTableNameWithSchema() {
        return getCommitPropertyTableName().nameWithSchema();
    }

    public DBObjectName getCommitPropertyTableName() {
        return new DBObjectName(schemaName, COMMIT_PROPERTY_TABLE_NAME);
    }

    public String getSnapshotTableNameWithSchema() {
        return getSnapshotTableName().nameWithSchema();
    }

    public DBObjectName getSnapshotTableName() {
        return new DBObjectName(schemaName, SNAPSHOT_TABLE_NAME);
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
}
