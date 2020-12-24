package org.javers.core.pico;

import org.javers.core.CoreConfiguration;
import org.picocontainer.MutablePicoContainer;

/**
 * @author bartosz.walacik
 */
public abstract class LateInstantiatingModule extends InstantiatingModule {

    private final CoreConfiguration configuration;

    public LateInstantiatingModule(CoreConfiguration configuration, MutablePicoContainer container) {
        super(container);
        this.configuration = configuration;
    }

    protected CoreConfiguration getConfiguration() {
        return configuration;
    }
}
