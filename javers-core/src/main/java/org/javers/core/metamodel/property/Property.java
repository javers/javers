package org.javers.core.metamodel.property;

import java.lang.reflect.Type;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public interface Property {

    String getName();

    Type getGenericType();

    Class<?> getType();

    /**
     * true if property looks like identifier of an Entity, for example has @Id annotation
     */
    boolean looksLikeId();

    /**
     * returns property value, even if private
     *
     * @param target invocation target
     */
    Object get(Object target);

    boolean isNull(Object target);
}
