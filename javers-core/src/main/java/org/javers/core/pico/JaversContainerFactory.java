package org.javers.core.pico;

import org.javers.common.pico.JaversModule;
import org.javers.common.validation.Validate;
import org.javers.core.JaversCoreConfiguration;
import org.javers.model.pico.ModelJaversModule;
import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.containers.PropertiesPicoContainer;

import java.util.*;

/**
 * @author Piotr Betkier
 * @author bartosz walacik
 */
public class JaversContainerFactory {

    public static PicoContainer create(List<JaversModule> modules, List<?> beans) {
        Validate.argumentIsNotNull(modules);

        MutablePicoContainer container = new DefaultPicoContainer();

        for (JaversModule module : modules) {
            for (Class component : module.getModuleComponents()) {
                addComponent(container, component);
            }
        }

        if (beans != null) {
            for (Object bean : beans) {
                addComponent(container, bean);
            }
        }

        return container;
    }

    private static void addComponent(MutablePicoContainer container, Object classOrInstance) {
        container.as(Characteristics.CACHE).addComponent(classOrInstance);
    }

    public static PicoContainer createDefaultCore() {
        List<JaversModule> coreModules = Arrays.asList(new CoreJaversModule(), new ModelJaversModule(new JaversCoreConfiguration()));
        return create(coreModules,null);
    }
}
