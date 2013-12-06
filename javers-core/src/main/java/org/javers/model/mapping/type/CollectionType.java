package org.javers.model.mapping.type;

import org.javers.common.validation.Validate;

import java.lang.reflect.Type;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public class CollectionType extends ContainerType {

    private Class elementType;

    public CollectionType(Type genericJavaType) {
        super(genericJavaType);
    }

    /**
     * When Collection is parametrized type,
     * returns JaversType of type argument.
     * <br/>
     * For example, if baseJavaType = List<String>, returns JaversType of String
     */
    public JaversType getElementType() {
        return null;
    }
}
