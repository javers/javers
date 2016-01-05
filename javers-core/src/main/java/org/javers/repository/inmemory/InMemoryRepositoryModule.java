package org.javers.repository.inmemory;

import org.javers.common.collections.Lists;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class InMemoryRepositoryModule extends InstantiatingModule {

    public InMemoryRepositoryModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return Lists.asList(
            (Class) InMemoryRepository.class
        );
    }
}
