package org.javers.core;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.pico.InstantiatingModule;
import org.javers.core.pico.JaversModule;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;

import java.util.List;

/**
 * @author bartosz walacik
 */
public abstract class AbstractJaversBuilder {

    private MutablePicoContainer container;

    protected <T> T getContainerComponent(Class<T> ofClass) {
        checkIfBuilt();
        return container.getComponent(ofClass);
    }

    protected void bootContainer() {
        container = new DefaultPicoContainer(new Caching());
    }

    protected void addModule(InstantiatingModule module) {
        checkIfBuilt();
        module.instantiateAndBindComponents();
    }

    protected void addModule(JaversModule module) {
        checkIfBuilt();
        for (Class component : module.getComponents()) {
            addComponent(component);
        }
    }

    protected <T> List<T> getComponents(Class<T> ofType){
        return container.getComponents(ofType);
    }

    protected MutablePicoContainer getContainer() {
        return container;
    }

    protected void addComponent(Object classOrInstance) {
        checkIfBuilt();
        container.addComponent(classOrInstance);
    }

    protected void bindComponent(Object componentKey, Object implementationOrInstance) {
        checkIfBuilt();
        container.addComponent(componentKey, implementationOrInstance);
    }

    protected void removeComponent(Object classOrInstance) {
        checkIfBuilt();
        container.removeComponent(classOrInstance);
    }
    
    private void checkIfNotBuilt() {
        if (isBuilt()) {
            throw new JaversException(JaversExceptionCode.ALREADY_BUILT);
        }
    }

    private void checkIfBuilt() {
        if (!isBuilt()) {
            throw new JaversException(JaversExceptionCode.CONTAINER_NOT_READY);
        }
    }

    private boolean isBuilt() {
        return container != null;
    }
}
