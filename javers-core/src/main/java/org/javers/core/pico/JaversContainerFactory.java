package org.javers.core.pico;

import org.javers.common.pico.JaversModule;
import org.javers.core.MappingStyle;
import org.javers.model.pico.ModelJaversModule;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;

public class JaversContainerFactory {

    public static PicoContainer create(MappingStyle configuredStyle) {
        JaversModule[] modules = {new CoreJaversModule(), new ModelJaversModule(configuredStyle)};

        DefaultPicoContainer javersContainer = new DefaultPicoContainer();
        for (JaversModule module : modules) {
            module.addModuleComponentsTo(javersContainer);
        }

        return javersContainer;
    }
}
