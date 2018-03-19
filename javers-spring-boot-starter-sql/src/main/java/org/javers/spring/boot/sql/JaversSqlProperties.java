package org.javers.spring.boot.sql;

import org.javers.spring.JaversSpringProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "javers")
public class JaversSqlProperties extends JaversSpringProperties {

    private boolean sqlSchemaManagementEnabled = true;

    public boolean isSqlSchemaManagementEnabled() {
        return sqlSchemaManagementEnabled;
    }

    public void setSqlSchemaManagementEnabled(boolean sqlSchemaManagementEnabled) {
        this.sqlSchemaManagementEnabled = sqlSchemaManagementEnabled;
    }
}
