package org.javers.core;

import org.javers.common.pico.JaversModule;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.core.pico.JaversContainerFactory;
import org.picocontainer.PicoContainer;

import java.util.Arrays;
import java.util.List;

/**
 * @author bartosz walacik
 */
public abstract class AbstractJaversBuilder {

    private PicoContainer container;

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

    protected PicoContainer bootContainer(JaversModule module) {
        return bootContainer(Arrays.asList(module),null);
    }

    protected PicoContainer bootContainer(JaversModule module, List<?> beans) {
        return bootContainer(Arrays.asList(module),beans);
    }

    protected PicoContainer bootContainer(List<JaversModule> modules, List<?> beans) {
        checkIfNotBuilt();
        container = JaversContainerFactory.create(modules, beans);
        return container;
    }
}
