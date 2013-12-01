package org.javers.model.mapping;

import org.javers.model.mapping.type.JaversType;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public interface Property {

    String getName();

    JaversType getType();

    //TODO move to Snapshoot/Wrapper
    void setValue(Object value);

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
