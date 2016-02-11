package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.MappingStyle;
import org.javers.core.pico.InstantiatingModule;
import org.javers.core.pico.LateInstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

public class TailoredJaversMemberFactoryModule extends LateInstantiatingModule {

    public TailoredJaversMemberFactoryModule(JaversCoreConfiguration configuration, MutablePicoContainer container) {
        super(configuration, container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        MappingStyle mappingStyle = getConfiguration().getMappingStyle();

        if (mappingStyle == MappingStyle.BEAN) {
            return (Collection) Lists.asList(TailoredJaversMethodFactory.class);
        } else if (mappingStyle == MappingStyle.FIELD) {
            return (Collection) Lists.asList(TailoredJaversFieldFactory.class);
        } else {
            throw new RuntimeException("not implementation for " + mappingStyle);
        }
    }
}
