package org.javers.core.pico;

/**
 * @author bartosz walacik
 */

import org.picocontainer.MutablePicoContainer;

public abstract class InstantiatingModule {

    private MutablePicoContainer container;

    public InstantiatingModule(MutablePicoContainer container) {
        this.container = container;
    }

    public abstract void instantiateAndBindComponents();

    protected void addComponent(Object component){
        JaversContainerUtil.addComponent(container, component);
    }

    protected <T> T getComponent(Class<T> componentType) {
        return container.getComponent(componentType);
    }
}
