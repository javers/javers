package org.javers.core.pico;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.ArgumentResolver;
import org.picocontainer.PicoContainer;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author bartosz walacik
 */
public class ContainerArgumentResolver implements ArgumentResolver {
    private static final Logger logger = getLogger(ContainerArgumentResolver.class);

    private final PicoContainer container;

    public ContainerArgumentResolver(PicoContainer container) {
        this.container = container;
    }

    @Override
    public Object resolve(Class argType) {
        if (argType == PicoContainer.class){
            return container;
        }
        Object component = container.getComponent(argType);

        if (component == null) {
            logger.error("failed to get component of type "+argType.getName()+" from Pico container");
            throw new JaversException(JaversExceptionCode.COMPONENT_NOT_FOUND, argType.getName());
        }

        return component;
    }
}
