package org.javers.core.metamodel.clazz;

import org.javers.common.pico.JaversModule;
import org.javers.common.validation.Validate;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.MappingStyle;
import org.javers.core.metamodel.property.BeanBasedPropertyScanner;
import org.javers.core.metamodel.property.FieldBasedPropertyScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Piotr Betkier
 */
public class ManagedClassFactoryModule implements JaversModule {
    private static final Logger logger = LoggerFactory.getLogger(ManagedClassFactoryModule.class);

    private static final Class[] moduleComponents = new Class[] {ManagedClassFactory.class,
                                                                 ClassAnnotationsScanner.class};

    private static final Map<MappingStyle, Class> propertyScannersMapping = new HashMap() {{
        put(MappingStyle.BEAN, BeanBasedPropertyScanner.class);
        put(MappingStyle.FIELD, FieldBasedPropertyScanner.class);
    }};

    private final JaversCoreConfiguration javersConfiguration;

    public ManagedClassFactoryModule(JaversCoreConfiguration javersConfiguration) {
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