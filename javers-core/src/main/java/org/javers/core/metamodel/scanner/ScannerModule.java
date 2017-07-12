package org.javers.core.metamodel.scanner;

import org.javers.common.collections.Lists;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.MappingStyle;
import org.javers.core.pico.LateInstantiatingModule;
import org.picocontainer.MutablePicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class ScannerModule extends LateInstantiatingModule {
    private static final Logger logger = LoggerFactory.getLogger(ScannerModule.class);

    public ScannerModule(JaversCoreConfiguration configuration, MutablePicoContainer container) {
        super(configuration, container);
    }

    @Override
    protected Collection<Class> getImplementations() {

        MappingStyle mappingStyle = getConfiguration().getMappingStyle();
        logger.info("using "+mappingStyle.name()+ " mappingStyle");

        Class<? extends PropertyScanner> usedPropertyScanner;
        if (mappingStyle == MappingStyle.BEAN){
            usedPropertyScanner = BeanBasedPropertyScanner.class;
        } else if (mappingStyle == MappingStyle.FIELD){
            usedPropertyScanner = FieldBasedPropertyScanner.class;
        } else{
            throw new RuntimeException("not implementation for "+mappingStyle);
        }

        return (Collection) Lists.asList(
                ClassScanner.class,
                ClassAnnotationsScanner.class,
                AnnotationNamesProvider.class,
                usedPropertyScanner
        );
    }
}
