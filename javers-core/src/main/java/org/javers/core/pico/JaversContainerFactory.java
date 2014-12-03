package org.javers.core.pico;

import org.javers.common.pico.JaversModule;
import org.javers.common.validation.Validate;
import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

import java.util.*;

/**
 * @author Piotr Betkier
 * @author bartosz walacik
 */
public class JaversContainerFactory {

    public static MutablePicoContainer create(List<JaversModule> modules, List<?> beans) {
        Validate.argumentIsNotNull(modules);

        MutablePicoContainer container = new DefaultPicoContainer();

        for (JaversModule module : modules) {
            addModule(container, module);
        }

        if (beans != null) {
            for (Object bean : beans) {
                addComponent(container, bean);
            }
        }

        return container;
    }

    public static void addModule(MutablePicoContainer container, JaversModule module) {
        for (Class component : module.getModuleComponents()) {
            addComponent(container, component);
        }
    }

    public static void addComponent(MutablePicoContainer container, Object classOrInstance) {
        container.as(Characteristics.CACHE).addComponent(classOrInstance);
    }

}
