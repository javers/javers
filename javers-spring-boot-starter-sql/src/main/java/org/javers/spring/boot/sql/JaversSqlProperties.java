package org.javers.spring.boot.sql;

import org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook;
import org.javers.spring.JaversSpringProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "javers")
public class JaversSqlProperties extends JaversSpringProperties {
    private static final String DEFAULT_OBJECT_ACCESS_HOOK = HibernateUnproxyObjectAccessHook.class.getName();

    private boolean sqlSchemaManagementEnabled = true;
    private boolean sqlJsonTypeSupportEnabled = false;
    private boolean sqlGlobalIdCacheDisabled = false;
    private String sqlSchema;
    private String sqlGlobalIdTableName;
    private String sqlCommitTableName;
    private String sqlSnapshotTableName;
    private String sqlCommitPropertyTableName;

    public boolean isSqlSchemaManagementEnabled() {
        return sqlSchemaManagementEnabled;
    }

    public void setSqlSchemaManagementEnabled(boolean sqlSchemaManagementEnabled) {
        this.sqlSchemaManagementEnabled = sqlSchemaManagementEnabled;
    }

    public String getSqlSchema() {
        return sqlSchema;
    }

    public void setSqlSchema(String sqlSchema) {
        this.sqlSchema = sqlSchema;
    }

    public boolean isSqlGlobalIdCacheDisabled() {
        return sqlGlobalIdCacheDisabled;
    }

    public void setSqlGlobalIdCacheDisabled(boolean sqlGlobalIdCacheDisabled) {
        this.sqlGlobalIdCacheDisabled = sqlGlobalIdCacheDisabled;
    }

    protected String defaultObjectAccessHook(){
        return DEFAULT_OBJECT_ACCESS_HOOK;
    }

    public String getSqlGlobalIdTableName() {
        return sqlGlobalIdTableName;
    }

    public void setSqlGlobalIdTableName(String sqlGlobalIdTableName) {
        this.sqlGlobalIdTableName = sqlGlobalIdTableName;
    }

    public String getSqlCommitTableName() {
        return sqlCommitTableName;
    }

    public void setSqlCommitTableName(String sqlCommitTableName) {
        this.sqlCommitTableName = sqlCommitTableName;
    }

    public String getSqlSnapshotTableName() {
        return sqlSnapshotTableName;
    }

    public void setSqlSnapshotTableName(String sqlSnapshotTableName) {
        this.sqlSnapshotTableName = sqlSnapshotTableName;
    }

    public String getSqlCommitPropertyTableName() {
        return sqlCommitPropertyTableName;
    }

    public void setSqlCommitPropertyTableName(String sqlCommitPropertyTableName) {
        this.sqlCommitPropertyTableName = sqlCommitPropertyTableName;
    }

    public boolean isSqlJsonTypeSupportEnabled() {
        return sqlJsonTypeSupportEnabled;
    }

    public void setSqlJsonTypeSupportEnabled(boolean sqlJsonTypeSupportEnabled) {
        this.sqlJsonTypeSupportEnabled = sqlJsonTypeSupportEnabled;
    }
}
