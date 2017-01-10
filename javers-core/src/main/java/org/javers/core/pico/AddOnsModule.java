package org.javers.core.pico;

import org.picocontainer.MutablePicoContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AddOnsModule extends InstantiatingModule{
    private final Set<Class> implementations;

    public AddOnsModule(MutablePicoContainer container, Collection<Class> implementations) {
        super(container);
        this.implementations = new HashSet<>(implementations);
    }

    @Override
    public Collection<Class> getImplementations() {
        return Collections.unmodifiableSet(implementations);
    }
}
