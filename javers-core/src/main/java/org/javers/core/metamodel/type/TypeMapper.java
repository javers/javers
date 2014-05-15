package org.javers.core.metamodel.type;

import org.javers.common.collections.Primitives;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.ManagedClassDefinition;
import org.javers.core.metamodel.property.Property;
import org.joda.time.LocalDate;
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

    private final ValueType OBJECT_TYPE = new ValueType(Object.class);
    private final TypeFactory typeFactory;
    private final Map<Type, JaversType> mappedTypes;

    public TypeMapper(TypeFactory typeFactory) {
        this.typeFactory = typeFactory;

        mappedTypes = new HashMap<>();

        //primitives & boxes
        for (Class primitiveOrBox : Primitives.getPrimitiveAndBoxTypes()) {
            registerPrimitiveType(primitiveOrBox);
        }

        //String & Enum
        registerPrimitiveType(String.class);
        registerPrimitiveType(Enum.class);

        //array
        addType(new ArrayType(Object[].class));

        //well known Value types
        registerValueType(LocalDateTime.class);
        registerValueType(LocalDate.class);
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

        if (javaType == Object.class){
            return OBJECT_TYPE;
        }

        JaversType jType = getExactMatchingJaversType(javaType);
        if (jType != null) {
            return jType;
        }

        return createMapping(javaType);
    }

    /**
     * @throws JaversException CLASS_NOT_MANAGED if given javaClass is NOT mapped to {@link ManagedType}
     */
    public ManagedType getJaversManagedType(Class javaType) {
        JaversType javersType = getJaversType(javaType);

        if (!(javersType instanceof  ManagedType)){
            throw new JaversException(JaversExceptionCode.CLASS_NOT_MANAGED,
                                      javaType.getName(),
                                      javersType.getClass().getSimpleName()) ;
        }

        return (ManagedType)javersType;
    }

  /*  public List<JaversType> getJaversTypes(List<Class> javaTypes) {
        argumentIsNotNull(javaTypes);
        return Lists.transform(javaTypes, new Function<Class, JaversType>() {
            public JaversType apply(Class javaType) {
                return getJaversType(javaType);
            }
        });
    } */

    public <T extends JaversType> T getPropertyType(Property property){
        argumentIsNotNull(property);
        return (T) getJaversType(property.getGenericType());
    }

    public boolean isEntityReferenceOrValueObject(Property property){
        JaversType javersType = getPropertyType(property);
        return javersType instanceof ManagedType;
    }

    /**
     * is Set, List or Array of ManagedClasses
     *
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED if property type is not fully parametrized
     */
    public boolean isContainerOfManagedClasses(EnumerableType enumerableType){
        if (! (enumerableType instanceof ContainerType)) {
            return false;
        }

        return getJaversType(((ContainerType) enumerableType).getItemClass()) instanceof ManagedType;
    }

    /**
     * is Map with ManagedClass on Key or Value position
     *
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED if property type is not fully parametrized
     */
    public boolean isMapWithManagedClass(EnumerableType enumerableType) {
        if (! (enumerableType instanceof MapType)) {
            return false;
        }

        MapType mapType = (MapType)enumerableType;

        JaversType keyType = getJaversType(mapType.getKeyClass());
        JaversType valueType = getJaversType(mapType.getValueClass());

        return keyType instanceof ManagedType || valueType instanceof ManagedType;
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

    protected <T extends JaversType> List<T> getMappedTypes(Class<T> ofType) {
        List<T> result = new ArrayList<>();
        for(JaversType jType : mappedTypes.values()) {
            if(ofType.isAssignableFrom(jType.getClass()) ) {
                result.add((T)jType);
            }
        }
        return result;
    }

    public boolean isPrimitiveOrValue(Class clazz) {
        JaversType jType  = getJaversType(clazz);
        return  jType instanceof PrimitiveOrValueType;
    }

    public Class getDehydratedType(Class expectedType){
        JaversType expectedJaversType = getJaversType(expectedType);

        if (expectedJaversType instanceof ManagedType){
            return GlobalCdoId.class;
        }
        else {
            return expectedType;
        }
    }

    //-- private

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
