package org.javers.model.mapping.type;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.Entity;

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
        registerPrimitiveType(Integer.TYPE);
        registerPrimitiveType(Boolean.TYPE);
        registerPrimitiveType(Double.TYPE);
        registerPrimitiveType(Float.TYPE);

        //primitive boxes
        registerPrimitiveType(Integer.class);
        registerPrimitiveType(Boolean.class);
        registerPrimitiveType(Double.class);
        registerPrimitiveType(Float.class);
        registerPrimitiveType(String.class);
        registerPrimitiveType(Enum.class);

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

    private void addType(JaversType type) {
        mappedTypes.add(type);
    }

    public void registerPrimitiveType(Class<?> primitiveClass){
        addType(new PrimitiveType(primitiveClass));
    }

    public void registerReferenceType(Class<?> entityClass){
        addType(new ReferenceType(entityClass));
    }
}
