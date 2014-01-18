package org.javers.core;

import org.javers.common.pico.JaversModule;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.core.pico.JaversContainerFactory;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

import java.util.Arrays;
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

    private void checkIfNotBuilt() {
        if (isBuilt()) {
            throw new JaversException(JaversExceptionCode.ALREADY_BUILT);
        }
    }

    protected void checkIfBuilt() {
        if (!isBuilt()) {
            throw new JaversException(JaversExceptionCode.CONTAINER_NOT_READY);
        }
    }

    protected boolean isBuilt() {
        return container != null;
    }


    protected PicoContainer bootContainer(JaversModule modules, Object... beans) {
        return bootContainer(modules, Arrays.asList(beans));
    }

    protected PicoContainer bootContainer(JaversModule module, List<?> beans) {
        checkIfNotBuilt();
        container = JaversContainerFactory.create(Arrays.asList(module), beans);
        return container;
    }

    protected void addComponent(Object classOrInstance) {
        checkIfBuilt();
        JaversContainerFactory.addComponent(container, classOrInstance);
    }

    protected void addModule(JaversModule module) {
        checkIfBuilt();
        JaversContainerFactory.addModule(container, module);
    }
}
