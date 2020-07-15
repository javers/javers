package org.javers.repository.sql.schema;

import java.util.Optional;
import org.javers.repository.sql.SqlRepositoryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author Ian Agius
 */
public class TableNameProvider {
    private static final Logger logger = LoggerFactory.getLogger(TableNameProvider.class);

    private final Optional<String> schemaName;
    private final Optional<String> globalIdTableName;
    private final Optional<String> commitTableName;
    private final Optional<String> snapshotTableName;
    private final Optional<String> commitPropertyTableName;

    public TableNameProvider(SqlRepositoryConfiguration configuration) {
        this.schemaName = configuration.getSchemaNameAsOptional();
        this.globalIdTableName = configuration.getGlobalIdTableNameAsOptional();
        this.commitTableName = configuration.getCommitTableNameAsOptional();
        this.snapshotTableName = configuration.getSnapshotTableNameAsOptional();
        this.commitPropertyTableName = configuration.getCommitPropertyTableNameAsOptional();

        logger.info("Commit table:          {}", getCommitTableNameWithSchema());
        logger.info("CommitProperty table:  {}", getCommitPropertyTableNameWithSchema());
        logger.info("GlobalId table:        {}", getGlobalIdTableNameWithSchema());
        logger.info("Snapshot table:        {}", getSnapshotTableNameWithSchema());
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
        return new DBObjectName(schemaName,
                snapshotTableName.orElse(SNAPSHOT_TABLE_NAME) + "_" + SNAPSHOT_TABLE_PK_SEQ).nameWithSchema();
    }

    public String getGlobalIdPkSeqWithSchema() {
        return new DBObjectName(schemaName,
                globalIdTableName.orElse(GLOBAL_ID_TABLE_NAME) + "_" + GLOBAL_ID_PK_SEQ).nameWithSchema();
    }

    public String getCommitPkSeqWithSchema() {
        return new DBObjectName(schemaName,
                commitTableName.orElse(COMMIT_TABLE_NAME) + "_" + COMMIT_PK_SEQ).nameWithSchema();
    }

    /**
     * used only by migration scripts
     */
    @Deprecated
    public String getCdoClassTableNameWithSchema() {
        return new DBObjectName(schemaName, "jv_cdo_class").nameWithSchema();
    }

    DBObjectName getGlobalIdTableName() {
        return new DBObjectName(schemaName, globalIdTableName.orElse(GLOBAL_ID_TABLE_NAME));
    }

    DBObjectName getCommitTableName() {
        return new DBObjectName(schemaName, commitTableName.orElse(COMMIT_TABLE_NAME));
    }

    DBObjectName getCommitPropertyTableName() {
        return new DBObjectName(schemaName, commitPropertyTableName.orElse(COMMIT_PROPERTY_TABLE_NAME));
    }

    DBObjectName getSnapshotTableName() {
        return new DBObjectName(schemaName, snapshotTableName.orElse(SNAPSHOT_TABLE_NAME));
    }

    Optional<String> getSchemaName() {
        return schemaName;
    }
}
