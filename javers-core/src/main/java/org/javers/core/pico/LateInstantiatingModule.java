package org.javers.core.pico;

import org.javers.core.JaversCoreConfiguration;
import org.picocontainer.MutablePicoContainer;

/**
 * @author bartosz.walacik
 */
public abstract class LateInstantiatingModule extends InstantiatingModule {

    private final JaversCoreConfiguration configuration;

    public LateInstantiatingModule(JaversCoreConfiguration configuration, MutablePicoContainer container) {
        super(container);
        this.configuration = configuration;
    }

    protected JaversCoreConfiguration getConfiguration() {
        return configuration;
    }
}
