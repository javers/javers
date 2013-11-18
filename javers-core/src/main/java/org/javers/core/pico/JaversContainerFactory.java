package org.javers.core.pico;

import org.javers.common.pico.JaversModule;
import org.javers.common.validation.Validate;
import org.javers.core.JaversConfiguration;
import org.javers.core.MappingStyle;
import org.javers.model.pico.ModelJaversModule;
import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.containers.PropertiesPicoContainer;

import java.util.*;

/**
 * @author Piotr Betkier
 */
public class JaversContainerFactory {

    public static PicoContainer create(JaversConfiguration configuration, List<JaversModule> externalModules) {
        Validate.argumentIsNotNull(configuration);
        Validate.argumentIsNotNull(externalModules);

        List<JaversModule> modules = new ArrayList<>();
        modules.addAll(getCoreModules(configuration));
        modules.addAll(externalModules);

        MutablePicoContainer javersContainer = new DefaultPicoContainer(new PropertiesPicoContainer(configuration.getProperties()));

        for (JaversModule module : modules) {
            for (Class component : module.getModuleComponents()) {
                javersContainer.as(Characteristics.USE_NAMES,Characteristics.CACHE).addComponent(component);
            }
        }

        return javersContainer;
    }

    /**
     * creates JaversContainer without external modules
     */
    public static PicoContainer create(JaversConfiguration configuration) {
        return create(configuration, Collections.EMPTY_LIST);
    }

    /**
     * creates JaversContainer with default properties
     */
    public static PicoContainer create() {
        return create(new JaversConfiguration(), Collections.EMPTY_LIST);
    }

    private static List<JaversModule> getCoreModules(JaversConfiguration configuratione) {
        return Arrays.asList(new CoreJaversModule(), new ModelJaversModule(configuratione));
    }


}
