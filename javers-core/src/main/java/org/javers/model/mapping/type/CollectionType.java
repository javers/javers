package org.javers.model.mapping.type;

import org.javers.common.validation.Validate;

import java.lang.reflect.Type;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public class CollectionType extends ContainerType {

    private final Class elementType;

    public CollectionType(Type genericJavaType) {
        super(genericJavaType);

        if (getActualClassTypeArguments().size() == 1) {
            elementType = getActualClassTypeArguments().get(0);
        } else {
            elementType = null;
        }
    }

    /**
     * Collection content type,
     * when Collection is generic Type with exact one actual Class argument
     * <br/>
     * For example, if baseJavaType = List<String>, returns String.class
     */
    public Class getElementType() {
        return elementType;
    }
}
