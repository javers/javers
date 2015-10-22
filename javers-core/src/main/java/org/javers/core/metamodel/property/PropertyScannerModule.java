package org.javers.core.metamodel.property;

import org.javers.common.collections.Lists;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.MappingStyle;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class PropertyScannerModule extends InstantiatingModule {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PropertyScannerModule.class);

    private final JaversCoreConfiguration coreConfiguration;

    public PropertyScannerModule(MutablePicoContainer container, JaversCoreConfiguration coreConfiguration) {
        super(container);
        this.coreConfiguration = coreConfiguration;
    }

    @Override
    protected Collection<Class> getImplementations() {

        MappingStyle mappingStyle = coreConfiguration.getMappingStyle();
        logger.info("using "+mappingStyle.name()+ " mappingStyle");

        if (mappingStyle == MappingStyle.BEAN){
            return (Collection) Lists.asList(BeanBasedPropertyScanner.class);
        } else if (mappingStyle == MappingStyle.FIELD){
            return (Collection) Lists.asList(FieldBasedPropertyScanner.class);
        } else{
            throw new RuntimeException("not implementation for "+mappingStyle);
        }
    }
}
