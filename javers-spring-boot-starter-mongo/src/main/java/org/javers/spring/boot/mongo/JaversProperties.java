package org.javers.spring.boot.mongo;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author pawelszymczyk
 */
@ConfigurationProperties(prefix = "javers")
public class JaversProperties {

    private String algorithm = "simple";
    private String mappingStyle = "field";
    private boolean newObjectSnapshot = false;
    private boolean prettyPrint = true;
    private boolean typeSafeValues = false;
    private String packagesToScan = "";

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
}
