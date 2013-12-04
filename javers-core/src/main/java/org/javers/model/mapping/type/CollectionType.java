package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class CollectionType extends ContainerType {

    private Class elementType;

    public CollectionType(Class baseJavaType) {
        super(baseJavaType);
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

    //TODO
    /*
    @Override
   .. public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CollectionType that = (CollectionType) o;

        if (elementType != null ? !elementType.equals(that.elementType) : that.elementType != null) return false;

        return true;
    }

    @Override
   .. public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (elementType != null ? elementType.hashCode() : 0);
        return result;
    } */
}
