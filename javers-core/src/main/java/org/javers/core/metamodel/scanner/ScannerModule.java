package org.javers.core.metamodel.scanner;

import org.javers.common.collections.Lists;
import org.javers.core.CascadingMode;
import org.javers.core.JaversBuilder;
import org.javers.core.CoreConfiguration;
import org.javers.core.MappingStyle;
import org.javers.core.pico.LateInstantiatingModule;
import org.picocontainer.MutablePicoContainer;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * @author bartosz.walacik
 */
public class ScannerModule extends LateInstantiatingModule {
    private static final Logger logger = JaversBuilder.logger;

    public ScannerModule(CoreConfiguration configuration, MutablePicoContainer container) {
        super(configuration, container);
    }

    @Override
    protected Collection<Class> getImplementations() {

        MappingStyle mappingStyle = getConfiguration().getMappingStyle();
        logger.info("mappingStyle: "+mappingStyle.name());

        Class<? extends PropertyScanner> usedPropertyScanner;
        if (mappingStyle == MappingStyle.BEAN){
            usedPropertyScanner = BeanBasedPropertyScanner.class;
        } else if (mappingStyle == MappingStyle.FIELD){
            usedPropertyScanner = FieldBasedPropertyScanner.class;
        } else{
            throw new RuntimeException("not implementation for "+mappingStyle);
        }

        final var usedCascadeEvaluator = determineCascadeEvaluatorToUse();

        return (Collection) Lists.asList(
                ClassScanner.class,
                ClassAnnotationsScanner.class,
                AnnotationNamesProvider.class,
                usedPropertyScanner,
                usedCascadeEvaluator
        );
    }

    private Class<? extends CascadeFallbackEvaluator> determineCascadeEvaluatorToUse() {
        final var cascadingMode = getConfiguration().getCascadingMode();
        if (cascadingMode == CascadingMode.NONE){
            return CascadeFallbackEvaluator.NoneFallbackEvaluator.class;
        } else if (cascadingMode == CascadingMode.ALL){
            return CascadeFallbackEvaluator.AllFallbackEvaluator.class;
        } else if (cascadingMode == CascadingMode.VALUE_OBJECTS){
            return CascadeFallbackEvaluator.OnlyValueObjectFallbackEvaluator.class;
        } else{
            throw new RuntimeException("not implementation for "+ cascadingMode);
        }
    }
}
