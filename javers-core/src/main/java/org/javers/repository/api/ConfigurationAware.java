package org.javers.repository.api;

import org.javers.core.CoreConfiguration;

public interface ConfigurationAware {

    void setConfiguration(CoreConfiguration coreConfiguration);
}
