package org.javers.core.metamodel.clazz;

import org.javers.core.pico.JaversModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Piotr Betkier
 */
public class ManagedClassFactoryModule implements JaversModule {

    private static final Class[] moduleComponents = new Class[] {
            ManagedClassFactory.class
    };

    @Override
    public Collection<Class> getComponents() {
        Collection<Class> components = new ArrayList<>();
        Collections.addAll(components, moduleComponents);
        return components;
    }
}