package org.javers.spring.boot.sql;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "javers")
public class JaversProperties {

    private String algorithm = "simple";
    private String mappingStyle = "field";
    private boolean newObjectSnapshot = false;
    private boolean prettyPrint = true;
    private boolean typeSafeValues = false;
    private String packagesToScan = "";
    private boolean auditableAspectEnabled = true;
    private boolean springDataAuditableRepositoryAspectEnabled = true;
    private boolean sqlSchemaManagementEnabled = true;

    public String getAlgorithm() {
        return algorithm;
    }

    public String getMappingStyle() {
        return mappingStyle;
    }

    public boolean isNewObjectSnapshot() {
        return newObjectSnapshot;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public boolean isTypeSafeValues() {
        return typeSafeValues;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public void setMappingStyle(String mappingStyle) {
        this.mappingStyle = mappingStyle;
    }

    public void setNewObjectSnapshot(boolean newObjectSnapshot) {
        this.newObjectSnapshot = newObjectSnapshot;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public void setTypeSafeValues(boolean typeSafeValues) {
        this.typeSafeValues = typeSafeValues;
    }

	public String getPackagesToScan() {
		return packagesToScan;
	}

	public void setPackagesToScan(String packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

    public boolean isAuditableAspectEnabled() {
        return auditableAspectEnabled;
    }

    public void setAuditableAspectEnabled(boolean auditableAspectEnabled) {
        this.auditableAspectEnabled = auditableAspectEnabled;
    }

    public boolean isSpringDataAuditableRepositoryAspectEnabled() {
        return springDataAuditableRepositoryAspectEnabled;
    }

    public void setSpringDataAuditableRepositoryAspectEnabled(boolean springDataAuditableRepositoryAspectEnabled) {
        this.springDataAuditableRepositoryAspectEnabled = springDataAuditableRepositoryAspectEnabled;
    }

    public boolean isSqlSchemaManagementEnabled() {
        return sqlSchemaManagementEnabled;
    }

    public void setSqlSchemaManagementEnabled(boolean sqlSchemaManagementEnabled) {
        this.sqlSchemaManagementEnabled = sqlSchemaManagementEnabled;
    }
}
