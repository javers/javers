package org.javers.model.mapping.type;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Maps Java types into Javers types
 *
 * @author bartosz walacik
 */
public class TypeMapper {
    private List<JaversType> mappedTypes;

    public TypeMapper() {
        mappedTypes = new ArrayList<>();

        //primitives
        addType(new PrimitiveType(Integer.TYPE));
        addType(new PrimitiveType(Boolean.TYPE));
        addType(new PrimitiveType(Double.TYPE));
        addType(new PrimitiveType(Float.TYPE));

        //primitive boxes
        addType(new PrimitiveType(Integer.class));
        addType(new PrimitiveType(Boolean.class));
        addType(new PrimitiveType(Double.class));
        addType(new PrimitiveType(Float.class));
        addType(new PrimitiveType(String.class));
        addType(new PrimitiveType(Enum.class));

        //containers
        addType(new CollectionType(Set.class));
        addType(new CollectionType(List.class));

        //array
        addType(new ArrayType());
    }

    public JaversType mapType(Class javaType) {
        //TODO add cache?
        for (JaversType mappedType : mappedTypes) {
            if (mappedType.isMappingForJavaType(javaType)) {
                return mappedType;
            }
        }

        throw new JaversException(JaversExceptionCode.TYPE_NOT_MAPPED, javaType.getName());
    }

    public void addType(JaversType type) {
        mappedTypes.add(type);
    }

}
