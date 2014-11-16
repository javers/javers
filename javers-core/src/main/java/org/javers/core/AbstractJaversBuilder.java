package org.javers.core;

import org.javers.common.pico.JaversModule;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
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

    void checkIfBuilt() {
        if (!isBuilt()) {
            throw new JaversException(JaversExceptionCode.CONTAINER_NOT_READY);
        }
    }

    boolean isBuilt() {
        return container != null;
    }


    PicoContainer bootContainer(JaversModule module, Object... beans) {
        return bootContainer(module, Arrays.asList(beans));
    }

    protected PicoContainer bootContainer(JaversModule module, List<?> beans) {
        checkIfNotBuilt();
        container = JaversContainerFactory.create(Arrays.asList(module), beans);
        return container;
    }

    void addComponent(Object classOrInstance) {
        checkIfBuilt();
        JaversContainerFactory.addComponent(container, classOrInstance);
    }

    void addModule(JaversModule module) {
        checkIfBuilt();
        JaversContainerFactory.addModule(container, module);
    }
}
