package org.javers.spring.boot.sql;

import org.javers.spring.JaversSpringProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "javers")
public class JaversSqlProperties extends JaversSpringProperties {

    private boolean sqlSchemaManagementEnabled = true;
    private boolean sqlGlobalIdCacheDisabled = false;
    private String sqlSchema;

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
}
