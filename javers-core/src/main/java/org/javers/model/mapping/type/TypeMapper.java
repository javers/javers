package org.javers.model.mapping.type;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;

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
        registerPrimitiveType(Long.TYPE);

        //primitive boxes
        registerPrimitiveType(Integer.class);
        registerPrimitiveType(Boolean.class);
        registerPrimitiveType(Double.class);
        registerPrimitiveType(Float.class);
        registerPrimitiveType(String.class);
        registerPrimitiveType(Enum.class);
        registerPrimitiveType(Long.class);

        //containers
        addType(new CollectionType(Set.class));
        addType(new CollectionType(List.class));

        //array
        addType(new ArrayType());
    }

    public JaversType getJavesrType(Class javaType) {
        //TODO add cache?
        JaversType mappedType = findJavesType(javaType);
        if(mappedType != null) {
            return mappedType;
        }
        throw new JaversException(JaversExceptionCode.TYPE_NOT_MAPPED, javaType.getName());
    }

    public boolean isMapped(Class javaType) {
        return findJavesType(javaType) != null;
    }

    private JaversType findJavesType(Class javaType) {
        for (JaversType mappedType : mappedTypes) {
            if (mappedType.isMappingForJavaType(javaType)) {
                return mappedType;
            }
        }
        return null;
    }

    private void addType(JaversType type) {
        mappedTypes.add(type);
    }

    public void registerPrimitiveType(Class<?> primitiveClass) {
        addType(new PrimitiveType(primitiveClass));
    }

    public void registerEntityReferenceType(Class<?> entityClass) {
        addType(new EntityReferenceType(entityClass));
    }

    public void registerValueObjectType(Class<?> objectValue) {
        addType(new ValueObjectType(objectValue));

    }

    public <T extends JaversType> List<T> getMappedTypes(Class<T> ofType) {
        List<T> result = new ArrayList<>();
        for(JaversType jType : mappedTypes) {
            if(ofType.isAssignableFrom(jType.getClass()) ) {
                result.add((T)jType);
            }
        }
        return result;
    }

    public List<ValueObjectType> getMappedValueObjectTypes() {
        return getMappedTypes(ValueObjectType.class);
    }

    public List<EntityReferenceType> getMappedEntityReferenceTypes() {
        return getMappedTypes(EntityReferenceType.class);
    }

    /*
    public List<Class> getReferenceTypes() {
        List<Class> referenceClasses = new ArrayList<>();
        for(JaversType entry : mappedTypes) {
            if(entry.isReferencedType()) {
                referenceClasses.add((Class)entry.getBaseJavaType());
            }
        }
        return referenceClasses;
    }*/
}
