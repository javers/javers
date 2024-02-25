package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class GraphFactoryModule extends InstantiatingModule {
	
    private Class<? extends ObjectHasher> objectHasherImplementation;

    public GraphFactoryModule(MutablePicoContainer container, Class<? extends ObjectHasher> objectHasherImplementation) {
        super(container);
        this.objectHasherImplementation = objectHasherImplementation;
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
               LiveCdoFactory.class,
               CollectionsCdoFactory.class,
               LiveGraphFactory.class,
               objectHasherImplementation,
               ObjectGraphBuilder.class,
               ObjectAccessHookDoNothingImpl.class);
    }
}
