package org.javers.core;

import java.util.Collection;

/**
 * Creates JaVers instance based on your domain model metadata and custom configuration.
 *
 * @author bartosz walacik
 */
public interface JaversFactory {
    /**
     * Created JaVers instance with inferred domain model.
     * Uses reasonable defaults. This approach is sufficient for simple domain models.
     * <br/>
     * For  complex domains use {@link ???}
     *
     * <br/>
     * Uses pure Java Reflection, ignores annotations.
     *
     * @param entityClasses list of domain model entities
     */
    Javers create(Iterable<Class<?>> entityClasses);
}
