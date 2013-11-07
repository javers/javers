package org.javers.core.pico;

import org.javers.common.pico.JaversModule;
import org.javers.common.validation.Validate;
import org.javers.core.MappingStyle;
import org.javers.model.pico.ModelJaversModule;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.Caching;

import java.util.*;

/**
 * @author Piotr Betkier
 */
public class JaversContainerFactory {

    public static PicoContainer create(MappingStyle configuredStyle) {
        return create(configuredStyle, Collections.EMPTY_LIST);
    }

    public static PicoContainer create(MappingStyle configuredStyle, Collection<JaversModule> externalModules) {
        Validate.argumentIsNotNull(configuredStyle);
        Validate.argumentIsNotNull(externalModules);

        List<JaversModule> modules = new ArrayList<>();
        modules.addAll(getCoreModules(configuredStyle));
        modules.addAll(externalModules);

        DefaultPicoContainer javersContainer = new DefaultPicoContainer(new Caching());
        for (JaversModule module : modules) {
            for (Class component : module.getModuleComponents()) {
                javersContainer.addComponent(component);
            }
        }

        return javersContainer;
    }

    private static List<JaversModule> getCoreModules(MappingStyle configuredStyle) {
        return Arrays.asList(new CoreJaversModule(),new ModelJaversModule(configuredStyle));
    }
}
