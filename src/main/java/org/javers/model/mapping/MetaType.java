package org.javers.model.mapping;

/**
 * High level value type
 *
 * @author bartosz walacik
 */
public enum MetaType {
    PRIMITIVE,
    PRIMITIVE_BOX,
    VALUE_OBJECT,
    /**
     * Reference to {@link org.javers.model.Entity}
     */
    REFERENCE,
    COLLECTION,
    ARRAY
}
