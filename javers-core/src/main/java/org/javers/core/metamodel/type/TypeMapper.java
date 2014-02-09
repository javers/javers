package org.javers.core.metamodel.type;

import org.javers.common.collections.Primitives;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.core.metamodel.property.*;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import static org.javers.common.reflection.ReflectionUtil.calculateHierarchyDistance;
import static org.javers.common.reflection.ReflectionUtil.extractClass;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Maps Java types into Javers types
 *
 * @author bartosz walacik
 */
public class TypeMapper {
    private static final Logger logger = LoggerFactory.getLogger(TypeMapper.class);

    private ManagedClassFactory managedClassFactory;
    private Map<Type, JaversType> mappedTypes;

    public TypeMapper(ManagedClassFactory managedClassFactory) {
        this.managedClassFactory = managedClassFactory;

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
        JaversType javersType = getPropertyType(property);
        if (! (javersType instanceof CollectionType)) {
            return false;
        }
        CollectionType collectionType = (CollectionType)javersType;

        if (collectionType.getElementType() == null) {
            return false;
        }

        JaversType elementType = getJaversType(collectionType.getElementType());

        return (elementType instanceof EntityType);
    }


    public void registerPrimitiveType(Class<?> primitiveClass) {
        addType(new PrimitiveType(primitiveClass));
    }

    public void registerManagedClass(ManagedClassDefinition def) {
        if (def instanceof ValueObjectDefinition) {
            ValueObject valueObject = managedClassFactory.create((ValueObjectDefinition) def);
            registerValueObjectType(valueObject);
        }
        if (def instanceof EntityDefinition) {
            Entity entity = managedClassFactory.create((EntityDefinition)def);
            registerEntityType(entity);
        }
        if (def instanceof  ValueDefinition) {
            registerValueType(def.getClazz());
        }
    }

    protected void registerValueObjectType(ValueObject valueObject) {
        addType(new ValueObjectType(valueObject));
    } 
    protected void registerEntityType(Entity entity) {
        addType(new EntityType(entity));
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

    public boolean isSupportedContainer(ContainerType propertyType) {
        return isPrimitiveOrValueOrObject(propertyType.getElementType());
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

    private boolean isMapped(Type javaType) {
        return mappedTypes.containsKey(javaType);
    }

    /**
     * @throws JaversExceptionCode TYPE_NOT_MAPPED
     */
    private JaversType spawnFromPrototype(Type javaType) {
        Class javaClass = extractClass(javaType);
        JaversType prototype = findPrototypeAssignableFrom(javaClass);

        JaversType spawned;
        if (prototype instanceof ManagedType) {
            spawned = ((ManagedType)prototype).spawn(javaClass, managedClassFactory);
        }
        else {
            spawned = prototype.spawn(javaType); //delegate to simple constructor
        }

        addType(spawned);

        return spawned;
    }

    /**
     * prototypes are non-generic
     *
     * @throws JaversExceptionCode TYPE_NOT_MAPPED
     */
    private JaversType findPrototypeAssignableFrom(Class javaClass) {
        argumentIsNotNull(javaClass);
        List<DistancePair> distances = new ArrayList<>();

        for (JaversType javersType : mappedTypes.values()) {
            DistancePair distancePair = new DistancePair(calculateHierarchyDistance(javaClass, javersType.getBaseJavaClass()), javersType);

            //this is due too spoiled Java Array reflection API
            if (javaClass.isArray()) {
                return getJaversType(Object[].class);
            }

            //just to better speed
            if (distancePair.distance == 1) {
                return distancePair.javersType;
            }

            distances.add(distancePair);
        }

        Collections.sort(distances);

        return distances.get(0).javersType;
    }

    private static class DistancePair implements Comparable<DistancePair> {
        Integer distance;
        JaversType javersType;

        DistancePair(Integer distance, JaversType javersType) {
            this.distance = distance;
            this.javersType = javersType;
        }

        @Override
        public int compareTo(DistancePair other) {
            return distance.compareTo(other.distance);
        }
    }
}
