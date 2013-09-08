package org.javers.model.mapping.type;

import static org.javers.common.validation.Validate.argumentShouldBeNull;

/**
 * @author bartosz walacik
 */
public class CollectionType extends ContainerType {

    private Class elementType;

    public CollectionType(Class javaType) {
        super(javaType);
    }

    public Class getElementType() {
        return elementType;
    }

    public void assignElementType(Class elementType) {
        argumentShouldBeNull(elementType, "Element type is already assigned it can not be change.");
        this.elementType = elementType;
    }
}
