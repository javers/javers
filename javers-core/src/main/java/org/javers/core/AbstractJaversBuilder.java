package org.javers.core;

import org.javers.core.pico.InstantiatingModule;
import org.javers.core.pico.JaversModule;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.pico.JaversContainerUtil;
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
        container = JaversContainerUtil.create(Arrays.asList(module), beans);
        return container;
    }

    void addComponent(Object classOrInstance) {
        checkIfBuilt();
        JaversContainerUtil.addComponent(container, classOrInstance);
    }

    void addModule(InstantiatingModule module) {
        module.instantiateAndBindComponents();
    }

    void addModule(JaversModule module) {
        checkIfBuilt();
        JaversContainerUtil.addModule(container, module);
    }

    <T> List<T> getComponents(Class<T> ofType){
        return container.getComponents(ofType);
    }

    MutablePicoContainer getContainer() {
        return container;
    }
}
