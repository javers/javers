package org.javers.core.metamodel.type;

import org.javers.common.collections.Lists;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class TypeMapperModule extends InstantiatingModule {

    public TypeMapperModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                TypeMapper.class
        );
    }
}
