package org.javers.core.metamodel.type;

import java.lang.reflect.Type;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public class CollectionType extends ContainerType {
    private transient Class elementType;

    public CollectionType(Type baseJavaType) {
        super(baseJavaType);
        if (getActualClassTypeArguments().size() == 1) {
            elementType =  getActualClassTypeArguments().get(0);
        }
    }

    @Override
    public Class getElementType() {
        return elementType;
    }
}
