package org.javers.spring.boot.sql;

import org.javers.spring.JaversSpringProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "javers")
public class JaversSqlProperties extends JaversSpringProperties {
    private static final String DEFAULT_OBJECT_ACCESS_HOOK = "org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook";

    private boolean sqlSchemaManagementEnabled = true;
    private String sqlSchema;
    private String objectAccessHook = DEFAULT_OBJECT_ACCESS_HOOK;

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

    public String getObjectAccessHook() {
        return objectAccessHook;
    }

    public void setObjectAccessHook(String objectAccessHook) {
        this.objectAccessHook = objectAccessHook;
    }
}
