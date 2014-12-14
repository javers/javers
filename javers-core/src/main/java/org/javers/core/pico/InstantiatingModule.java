package org.javers.core.pico;

/**
 * Helps to hide component classes in package-private scope
 *
 * @author bartosz walacik
 */

import org.javers.common.reflection.ReflectionUtil;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

public abstract class InstantiatingModule {

    private final MutablePicoContainer container;
    private final ContainerArgumentResolver argumentResolver;

    public InstantiatingModule(MutablePicoContainer container) {
        this.container = container;
        this.argumentResolver = new ContainerArgumentResolver(container);
    }

    public void instantiateAndBindComponents(){
        for (Class<?> implementation : getImplementations()){
            Object component = ReflectionUtil.newInstance(implementation,argumentResolver);
            container.addComponent(component);
        }
    }

    protected abstract Collection<Class> getImplementations();

}
