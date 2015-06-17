package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class GraphFactoryModule extends InstantiatingModule {
    public GraphFactoryModule(MutablePicoContainer container) {
        super(container);
    }
    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
               LiveCdoFactory.class,
               LiveGraphFactory.class,
               ObjectGraphBuilder.class,
               ObjectAccessHookDoNothingImpl.class);
    }
}
