package org.javers.spring.boot.sql;

import org.javers.repository.sql.DialectName;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "javers")
public class JaversProperties {

    private String algorithm = "simple";
    private String mappingStyle = "field";
    private boolean newObjectSnapshot = false;
    private boolean prettyPrint = true;
    private boolean typeSafeValues = false;

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

}
