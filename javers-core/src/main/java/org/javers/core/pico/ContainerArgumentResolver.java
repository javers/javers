package org.javers.core.pico;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.ArgumentResolver;
import org.picocontainer.PicoContainer;

/**
 * @author bartosz walacik
 */
public class ContainerArgumentResolver implements ArgumentResolver {

    private final PicoContainer container;

    public ContainerArgumentResolver(PicoContainer container) {
        this.container = container;
    }

    @Override
    public Object resolve(Class argType) {
        Object component = container.getComponent(argType);

        if (component == null) {
            throw new JaversException(JaversExceptionCode.COMPONENT_NOT_FOUND, argType.getName());
        }

        return component;
    }
}
