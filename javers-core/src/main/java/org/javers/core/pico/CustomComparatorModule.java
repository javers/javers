package org.javers.core.pico;

import org.javers.core.JaversCoreConfiguration;
import org.picocontainer.MutablePicoContainer;

import java.util.Map;

/**
 * @author akrystian
 */
public abstract class CustomComparatorModule extends LateInstantiatingModule{
    public CustomComparatorModule(JaversCoreConfiguration configuration, MutablePicoContainer container){
        super(configuration, container);
    }

    public abstract Map<Class,Class> getCustomComparatorMappings();

}
