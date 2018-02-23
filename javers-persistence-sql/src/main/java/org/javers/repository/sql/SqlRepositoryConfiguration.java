package org.javers.repository.sql;

import java.util.Optional;
import org.javers.common.validation.Validate;


/**
 * @author bartosz.walacik
 */
public class SqlRepositoryConfiguration {
    private final boolean globalIdCacheDisabled;
    private final String schemaName;
    private final boolean schemaManagementEnabled;

    SqlRepositoryConfiguration(boolean globalIdCacheDisabled, String schemaName, boolean schemaManagementEnabled) {
        Validate.argumentCheck(schemaName == null || !schemaName.isEmpty(),"schemaName should be null or non-empty");

        this.globalIdCacheDisabled = globalIdCacheDisabled;
        this.schemaName = schemaName;
        this.schemaManagementEnabled = schemaManagementEnabled;
    }

    public boolean isGlobalIdCacheDisabled() {
        return globalIdCacheDisabled;
    }

    /**
     * null or non-empty
     */
    public String getSchemaName() {
        return schemaName;
    }

    public Optional<String> getSchemaNameAsOptional() {
        return Optional.ofNullable(schemaName);
    }

    public boolean isSchemaManagementEnabled() {
        return schemaManagementEnabled;
    }
}
