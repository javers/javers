package org.javers.core.metamodel.type;

import org.javers.common.collections.Primitives;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.core.metamodel.property.Property;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Maps Java types into Javers types
 *
 * @author bartosz walacik
 */
public class TypeMapper {
    private static final Logger logger = LoggerFactory.getLogger(TypeMapper.class);

    private Map<Type, JaversType> mappedTypes;

    public TypeMapper() {
        mappedTypes = new HashMap<>();

        //primitives & boxes
        for (Class primitiveOrBox : Primitives.getPrimitiveAndBoxTypes()) {
            registerPrimitiveType(primitiveOrBox) ;
        }

        //String & Enum
        registerPrimitiveType(String.class);
        registerPrimitiveType(Enum.class);

        //array
        addType(new ArrayType(Object[].class));

        //well known Value types
        registerValueType(LocalDateTime.class);
        registerValueType(BigDecimal.class);
        registerValueType(Date.class);

        //Collections
        addType(new CollectionType(Set.class));
        addType(new CollectionType(List.class));

        //& Maps
        addType(new MapType(Map.class));
    }

    /**
     * returns mapped type or spawn new one from prototype
     *
     * @throws JaversExceptionCode TYPE_NOT_MAPPED
     */
    public JaversType getJaversType(Type javaType) {
        argumentIsNotNull(javaType);

        JaversType jType = getExactMatchingJaversType(javaType);
        if (jType != null) {
            return jType;
        }

        return spawnFromPrototype(javaType);
    }

    public JaversType getPropertyType(Property property){
        return getJaversType(property.getGenericType());
    }

    public boolean isEntityReferenceOrValueObject(Property property){
        JaversType javersType = getPropertyType(property);
        return (javersType instanceof EntityReferenceType ||
                javersType instanceof ValueObjectType);
    }

    public boolean isSupportedMap(MapType propertyType){
        return propertyType.isObjectToObjectMap() || isPrimitiveOrValue(propertyType.getEntryClass());
    }

    public boolean isCollectionOfEntityReferences(Property property){
        JaversType javersType = getPropertyType(property);
        if (! (javersType instanceof CollectionType)) {
            return false;
        }
        CollectionType collectionType = (CollectionType)javersType;

        if (collectionType.getElementType() == null) {
            return false;
        }

        JaversType elementType = getJaversType(collectionType.getElementType());

        return (elementType instanceof EntityReferenceType);
    }

    public <T extends Collection> void registerCollectionType(Class<T> collectionType) {
        addType(new CollectionType(collectionType));
    }

    public void registerPrimitiveType(Class<?> primitiveClass) {
        addType(new PrimitiveType(primitiveClass));
    }

    @Deprecated
    public void registerValueObjectType(Class<?> entityClass) {
        addType(new ValueObjectType(entityClass));
    }

    public void registerEntityReferenceType(Class<?> entityClass) {
        addType(new EntityReferenceType(entityClass));
    }

    public void registerValueType(Class<?> objectValue) {
        addType(new ValueType(objectValue));
    }

    public <T extends JaversType> List<T> getMappedTypes(Class<T> ofType) {
        List<T> result = new ArrayList<>();
        for(JaversType jType : mappedTypes.values()) {
            if(ofType.isAssignableFrom(jType.getClass()) ) {
                result.add((T)jType);
            }
        }
        return result;
    }

    //-- protected


    //-- private

    private boolean isPrimitiveOrValue(EntryClass entryClass) {
        JaversType keyType  = getJaversType(entryClass.getKey());
        JaversType valueType = getJaversType(entryClass.getValue());

        return  keyType instanceof PrimitiveOrValueType && valueType instanceof PrimitiveOrValueType;
    }

    private void addType(JaversType jType) {
        mappedTypes.put(jType.getBaseJavaType(), jType);
    }

    /**
     * @return null if not found
     */
    private JaversType getExactMatchingJaversType(Type javaType) {
        return mappedTypes.get(javaType);
    }

    private boolean isMapped(Type javaType) {
        return mappedTypes.containsKey(javaType);
    }

    /**
     * @throws JaversExceptionCode TYPE_NOT_MAPPED
     */
    private JaversType spawnFromPrototype(Type javaType) {
        JaversType prototype = findPrototypeAssignableFrom(javaType);

        JaversType spawned = prototype.spawn(javaType);

        addType(spawned);

        return spawned;
    }

    /**
     * prototypes are non-generic
     *
     * @throws JaversExceptionCode TYPE_NOT_MAPPED
     */
    private JaversType findPrototypeAssignableFrom(Type javaType) {
        argumentIsNotNull(javaType);

        for (JaversType javersType : mappedTypes.values()){
            if(javersType.mayBePrototypeFor(javaType)){
                return javersType;
            }
        }

        throw new JaversException(JaversExceptionCode.TYPE_NOT_MAPPED, javaType);
    }

}
