package org.javers.model.mapping;

import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.MetaType;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public interface Property {

    String getName();

    JaversType getType();

    /**
     * for ValueType.REFERENCE
     */
    Entity getRefEntity();

    //TODO move to Snapshoot/Wrapper
    Object getValue();

    //TODO move to Snapshoot/Wrapper
    void setValue(Object value);

}
