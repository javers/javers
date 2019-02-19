package org.javers.spring.boot.mongo;

import org.javers.spring.JaversSpringProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author pawelszymczyk
 */
@ConfigurationProperties(prefix = "javers")
public class JaversMongoProperties extends JaversSpringProperties {

    private boolean documentDbCompatibilityEnabled = false;

    public boolean isDocumentDbCompatibilityEnabled() {
        return documentDbCompatibilityEnabled;
    }

    public void setDocumentDbCompatibilityEnabled(boolean documentDbCompatibilityEnabled) {
        this.documentDbCompatibilityEnabled = documentDbCompatibilityEnabled;
    }

}
