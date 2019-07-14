package org.javers.core.pico;

/**
 * Helps to hide component classes in package-private scope
 *
 * @author bartosz walacik
 */

import org.javers.common.reflection.ReflectionUtil;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.AbstractAdapter;

import java.lang.reflect.Type;
import java.util.Collection;

public abstract class InstantiatingModule {

    private final MutablePicoContainer container;
    private final ContainerArgumentResolver argumentResolver;

    public InstantiatingModule(MutablePicoContainer container) {
        this.container = container;
        this.argumentResolver = new ContainerArgumentResolver(container);
    }

    public final void instantiateAndBindComponents(){
        for (Class<?> implementation : getImplementations()){
            ConstructorInjector constructorInjector = new ConstructorInjector(implementation);
            container.addAdapter(constructorInjector);
        }
    }

    protected abstract Collection<Class> getImplementations();

    private class ConstructorInjector extends AbstractAdapter {

        public ConstructorInjector(Class componentImplementation) {
            this(componentImplementation, componentImplementation);
        }

        public ConstructorInjector(Object componentKey, Class componentImplementation) {
            super(componentKey, componentImplementation);
        }

        @Override
        public Object getComponentInstance(PicoContainer pico, Type into) {
            return ReflectionUtil.newInstance(getComponentImplementation(), argumentResolver);
        }

        public void verify(PicoContainer container) {
        }

        @Override
        public String getDescriptor() {
            return getComponentKey().toString();
        }
    }

}
