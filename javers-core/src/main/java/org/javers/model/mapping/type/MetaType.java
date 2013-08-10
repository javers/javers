package org.javers.model.mapping.type;

/**
 * High level value type
 *
 * @author bartosz walacik
 */
@Deprecated
public enum MetaType {
    PRIMITIVE,
    PRIMITIVE_BOX,
    VALUE_OBJECT,
    /**
     * Reference to {@link org.javers.model.mapping.Entity}
     */
    REFERENCE,
    COLLECTION,
    ARRAY
}
