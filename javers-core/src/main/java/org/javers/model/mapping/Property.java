package org.javers.model.mapping;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public interface Property {

    String getName();

    MetaType getValueType();

    /**
     * for ValueType.REFERENCE
     */
    Entity getRefEntity();

    //TODO move to Snapshoot/Wrapper
    Object getValue();

    //TODO move to Snapshoot/Wrapper
    void setValue(Object value);

}
