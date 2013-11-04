package org.javers.core.pico;

import org.javers.common.pico.JaversModule;
import org.javers.core.MappingStyle;
import org.javers.model.pico.ModelJaversModule;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.Caching;

/**
 * @author Piotr Betkier
 */
public class JaversContainerFactory {

    public static PicoContainer create(MappingStyle configuredStyle) {
        JaversModule[] modules = {new CoreJaversModule(), new ModelJaversModule(configuredStyle)};

        DefaultPicoContainer javersContainer = new DefaultPicoContainer(new Caching());
        for (JaversModule module : modules) {
            for (Class component : module.getModuleComponents()) {
                javersContainer.addComponent(component);
            }
        }

        return javersContainer;
    }
}
