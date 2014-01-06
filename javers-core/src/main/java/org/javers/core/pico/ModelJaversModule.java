package org.javers.core.pico;

import org.javers.common.pico.JaversModule;
import org.javers.common.validation.Validate;
import org.javers.core.configuration.JaversCoreConfiguration;
import org.javers.core.MappingStyle;
import org.javers.model.mapping.BeanBasedPropertyScanner;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.FieldBasedPropertyScanner;
import org.javers.model.mapping.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Piotr Betkier
 */
public class ModelJaversModule implements JaversModule {
    private static final Logger logger = LoggerFactory.getLogger(ModelJaversModule.class);

    private static Class[] moduleComponents = new Class[] {EntityManager.class, TypeMapper.class, EntityFactory.class};

    private static Map<MappingStyle, Class> propertyScannersMapping = new HashMap() {{
        put(MappingStyle.BEAN, BeanBasedPropertyScanner.class);
        put(MappingStyle.FIELD, FieldBasedPropertyScanner.class);
    }};

    private JaversCoreConfiguration javersConfiguration;

    public ModelJaversModule(JaversCoreConfiguration javersConfiguration) {
        this.javersConfiguration = javersConfiguration;
    }

    @Override
    public Collection<Class> getModuleComponents() {
        Collection<Class> components = new ArrayList<>();
        Collections.addAll(components, moduleComponents);

        addPropertyScanner(components);
        
        return components;
    }

    private void addPropertyScanner(Collection<Class> components) {
        MappingStyle mappingStyle = javersConfiguration.getMappingStyle();
        logger.info("using "+mappingStyle.name()+ " mappingStyle");

        Validate.conditionFulfilled(propertyScannersMapping.containsKey(mappingStyle),
                "No PropertyScanner defined for " + mappingStyle);

        components.add(propertyScannersMapping.get(mappingStyle));
    }

}