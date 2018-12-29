package org.javers.repository.api;

import org.javers.core.JaversCoreConfiguration;

public interface ConfigurationAware {

    void setConfiguration(JaversCoreConfiguration coreConfiguration);
}
