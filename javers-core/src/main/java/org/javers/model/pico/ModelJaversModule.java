package org.javers.model.pico;

import org.javers.common.pico.JaversModule;
import org.javers.common.validation.Validate;
import org.javers.core.MappingStyle;
import org.javers.model.mapping.*;
import org.javers.model.mapping.type.TypeMapper;

import java.util.*;

/**
 * @author Piotr Betkier
 */
public class ModelJaversModule implements JaversModule {

    private static Class[] moduleComponents = new Class[] {EntityManager.class, TypeMapper.class, EntityFactory.class, ValueObjectFactory.class};

    private static Map<MappingStyle, Class> propertyScannersMapping = new HashMap() {{
        put(MappingStyle.BEAN, BeanBasedPropertyScanner.class);
        put(MappingStyle.FIELD, FieldBasedPropertyScanner.class);
    }};

    private MappingStyle configuredStyle;

    public ModelJaversModule(MappingStyle configuredStyle) {
        this.configuredStyle = configuredStyle;
    }

    @Override
    public Collection<Class> getModuleComponents() {
        Collection<Class> components = new ArrayList<>();
        Collections.addAll(components, moduleComponents);

        Validate.conditionFulfilled(propertyScannersMapping.containsKey(configuredStyle),
                "No PropertyScanner defined for " + configuredStyle);
        components.add(propertyScannersMapping.get(configuredStyle));
        return components;
    }

}