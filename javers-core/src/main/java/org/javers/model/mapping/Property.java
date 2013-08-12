package org.javers.model.mapping;

import org.javers.model.mapping.type.JaversType;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public interface Property {

    String getName();

    JaversType getType();

    //TODO move to Snapshoot/Wrapper
    Object getValue();

    //TODO move to Snapshoot/Wrapper
    void setValue(Object value);

}
