package org.javers.model.pico;

import org.javers.common.pico.JaversModule;
import org.javers.common.validation.Validate;
import org.javers.core.MappingStyle;
import org.javers.model.mapping.BeanBasedEntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.FieldBasedEntityFactory;
import org.javers.model.mapping.type.TypeMapper;

import java.util.*;

/**
 * @author Piotr Betkier
 */
public class ModelJaversModule implements JaversModule {

    private static Class[] moduleComponents = new Class[] {EntityManager.class, TypeMapper.class};

    private static Map<MappingStyle, Class> entityFactoriesMapping = new HashMap() {{
        put(MappingStyle.BEAN, BeanBasedEntityFactory.class);
        put(MappingStyle.FIELD, FieldBasedEntityFactory.class);
    }};

    private MappingStyle configuredStyle;

    public ModelJaversModule(MappingStyle configuredStyle) {
        this.configuredStyle = configuredStyle;
    }

    @Override
    public Collection<Class> getModuleComponents() {
        Collection<Class> components = new ArrayList<>();
        Collections.addAll(components, moduleComponents);

        Validate.conditionFulfilled(entityFactoriesMapping.containsKey(configuredStyle),
                                    "No EntityFactory defined for " + configuredStyle);
        components.add(entityFactoriesMapping.get(configuredStyle));
        return components;
    }

}