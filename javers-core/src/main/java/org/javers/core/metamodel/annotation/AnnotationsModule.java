package org.javers.core.metamodel.annotation;

import org.javers.common.collections.Lists;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class AnnotationsModule extends InstantiatingModule {

    public AnnotationsModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
            AnnotationNamesProvider.class,
            ClassAnnotationsScanner.class);
    }
}
