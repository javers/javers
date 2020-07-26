package org.javers.core.metamodel.type;

import org.javers.core.IgnoredClassesStrategy;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.util.Optional;

public class DynamicMappingStrategy {
    private static final Logger logger = TypeMapper.logger;

    //nullable
    private final IgnoredClassesStrategy ignoredClassesStrategy;

    public DynamicMappingStrategy(IgnoredClassesStrategy ignoredClassesStrategy) {
        this.ignoredClassesStrategy = ignoredClassesStrategy;
    }

    public DynamicMappingStrategy() {
        this.ignoredClassesStrategy = null;
    }

    Optional<JaversType> map(Type type) {
        if (ignoredClassesStrategy != null && type instanceof Class) {
            Class<?> clazz = (Class) type;
            if (ignoredClassesStrategy.isIgnored(clazz)) {
                logger.debug("javersType of '{}' mapped as IgnoredType by {}",
                        clazz.getSimpleName(),ignoredClassesStrategy.getClass().getName());
                return Optional.of(new IgnoredType(clazz));
            }
            return Optional.empty();
        }
        return Optional.empty();
    }
}
