package org.javers.core.metamodel.type;

import java.lang.reflect.Type;

/**
 * for lazy type loading
 */
public interface TypeMapperLazy {
    JaversType getJaversType(Type javaType);
}
