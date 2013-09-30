package org.javers.model.pico;

import org.javers.core.MappingStyle;
import org.javers.model.mapping.EntityFactory;
import org.picocontainer.PicoContainer;
import org.picocontainer.injectors.FactoryInjector;

import java.lang.reflect.Type;

public class EntityFactoryFactory extends FactoryInjector<EntityFactory> {

    public static final String CONFIGURED_MAPPING_STYLE_KEY = "configuredMappingStyle";

    public static String getEntityFactoryKeyName(MappingStyle style) {
        return "entityFactoryFor" + style;
    }

    @Override
    public EntityFactory getComponentInstance(PicoContainer container, Type into) {
        MappingStyle configuredStyle = (MappingStyle) container.getComponent(CONFIGURED_MAPPING_STYLE_KEY);
        return (EntityFactory) container.getComponent(getEntityFactoryKeyName(configuredStyle));
    }
}
