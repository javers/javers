package org.javers.core.metamodel.type;

import org.javers.common.collections.Primitives;
import org.javers.core.metamodel.property.*;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import static org.javers.common.reflection.ReflectionUtil.extractClass;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Maps Java types into Javers types
 *
 * @author bartosz walacik
 */
public class TypeMapper {
    private static final Logger logger = LoggerFactory.getLogger(TypeMapper.class);

    private final TypeFactory typeFactory;
    private final Map<Type, JaversType> mappedTypes;

    public TypeMapper(TypeFactory typeFactory) {
        this.typeFactory = typeFactory;

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
        addType(new SetType(Set.class));
        addType(new ListType(List.class));

        //& Maps
        addType(new MapType(Map.class));
    }

    /**
     * returns mapped type or spawns new one from prototype
     * or infers new one using default mapping
     */
    public JaversType getJaversType(Type javaType) {
        argumentIsNotNull(javaType);

        JaversType jType = getExactMatchingJaversType(javaType);
        if (jType != null) {
            return jType;
        }

        return createMapping(javaType);
    }

    /**
     * if given javaClass is mapped to {@link ManagedType}
     * returns {@link ManagedType#getManagedClass()}
     * @throws java.lang.IllegalArgumentException if given javaClass is NOT mapped to {@link ManagedType}
     */
    public ManagedClass getManagedClass(Class javaClass) {
        JaversType jType = getJaversType(javaClass);
        if (jType instanceof ManagedType) {
            return ((ManagedType)jType).getManagedClass();
        }
        throw new IllegalArgumentException("getManagedClass("+javaClass.getSimpleName()+") " +
                  "given javaClass is mapped to "+jType.getClass().getSimpleName()+", ManagedType expected");
    }

    public JaversType getPropertyType(Property property){
        return getJaversType(property.getGenericType());
    }

    public boolean isEntityReferenceOrValueObject(Property property){
        JaversType javersType = getPropertyType(property);
        return (javersType instanceof EntityType ||
                javersType instanceof ValueObjectType);
    }

    public boolean isSupportedMap(MapType propertyType){
        if (propertyType.getEntryClass() == null) {
            return false;
        }
        return isPrimitiveOrValueOrObject(propertyType.getEntryClass().getKey()) &&
               isPrimitiveOrValueOrObject(propertyType.getEntryClass().getValue());
    }

    public boolean isCollectionOfEntityReferences(Property property){
        return isCollectionOfType(property, EntityType.class);
    }

    public boolean isCollectionOfValueObjects(Property property) {
        return isCollectionOfType(property, ValueObjectType.class);
    }

    private boolean isCollectionOfType(Property property, Class<? extends ManagedType> managedType) {
        JaversType javersType = getPropertyType(property);
        if (! (javersType instanceof CollectionType)) {
            return false;
        }
        CollectionType collectionType = (CollectionType)javersType;

        if (collectionType.getElementType() == null) {
            return false;
        }

        JaversType elementType = getJaversType(collectionType.getElementType());

        return managedType.isAssignableFrom(elementType.getClass());
    }


    private void registerPrimitiveType(Class<?> primitiveClass) {
        addType(new PrimitiveType(primitiveClass));
    }

    public void registerManagedClass(ManagedClassDefinition def) {
        addType(typeFactory.createFromDefinition(def));
    }

    public void registerValueType(Class<?> objectValue) {
        addType(new ValueType(objectValue));
    }

    public boolean isSupportedContainer(ContainerType propertyType) {
        return isPrimitiveOrValueOrObject(propertyType.getElementType());
    }

    protected <T extends JaversType> List<T> getMappedTypes(Class<T> ofType) {
        List<T> result = new ArrayList<>();
        for(JaversType jType : mappedTypes.values()) {
            if(ofType.isAssignableFrom(jType.getClass()) ) {
                result.add((T)jType);
            }
        }
        return result;
    }

    //-- private

    //TODO
    private boolean isPrimitiveOrValueOrObject(Class clazz) {
        if (clazz == Object.class) {
            return true;
        }

        JaversType jType  = getJaversType(clazz);
        return  jType instanceof PrimitiveOrValueType || jType instanceof PrimitiveOrValueType;
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

    private JaversType createMapping(Type javaType) {
        argumentIsNotNull(javaType);
        JaversType prototype = findNearestAncestor(javaType);
        JaversType newType;

        if (prototype == null) {
            newType = typeFactory.infer(javaType);
        }
        else {
            newType = typeFactory.spawnFromPrototype(javaType, prototype);
        }

        addType(newType);
        return newType;
    }

    private JaversType findNearestAncestor(Type javaType) {
        Class javaClass = extractClass(javaType);
        List<DistancePair> distances = new ArrayList<>();

        for (JaversType javersType : mappedTypes.values()) {
            DistancePair distancePair = new DistancePair(javaClass, javersType);
            // logger.info("distance from "+javersType + ": "+distancePair.distance);

            //this is due too spoiled Java Array reflection API
            if (javaClass.isArray()) {
                return getJaversType(Object[].class);
            }

            //just to better speed
            if (distancePair.getDistance() == 1) {
                return distancePair.getJaversType();
            }

            distances.add(distancePair);
        }

        Collections.sort(distances);

        if (distances.get(0).isMax()){
            return null;
        }

        return distances.get(0).getJaversType();
    }
}
