package org.javers.model;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public interface Property {

    String getName();

    Object getValue();

    void setValue(Object value);


    // ValueObject?
    // Primitive?
    // Reference
    ValueType getType();

    boolean isAtomic();

    /**
     * for ValueType.REFERENCE
     */
    Entity getRefEntity();

}
