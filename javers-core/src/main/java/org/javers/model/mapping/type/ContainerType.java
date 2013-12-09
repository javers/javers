package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public abstract class ContainerType extends JaversType {
    private Class elementType;

    protected ContainerType(Type baseJavaType) {
        super(baseJavaType);

        elementType = initElementType();
    }

    protected abstract Class initElementType();

    /**
     * Collection/Array content type.
     * <br/>
     * When Collection is generic Type with exact one actual Class argument, returns this argument.
     * <br/>
     * When Array, returns ...
     * <br/>
     * For example, if baseJavaType = List<String>, returns String.class
     */
    public Class getElementType() {
        return elementType;
    }
}
