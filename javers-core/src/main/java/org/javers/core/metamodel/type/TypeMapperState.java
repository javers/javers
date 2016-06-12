package org.javers.core.metamodel.type;

import org.javers.common.collections.Function;
import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.javers.common.reflection.ReflectionUtil.extractClass;
import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * thread-safe, thin wrapper for map
 *
 * @author bartosz.walacik
 */
class TypeMapperState {
    private final Map<Type, JaversType> mappedTypes = new ConcurrentHashMap<>();
    private final Map<DuckType, Class> mappedTypeNames = new ConcurrentHashMap<>();
    private final TypeFactory typeFactory;
    private final ValueType OBJECT_TYPE = new ValueType(Object.class);

    TypeMapperState(TypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    /**
     * @throws JaversException TYPE_NAME_NOT_FOUND if given typeName is not registered
     * @since 1.4
     */
    Class getClassByTypeName(String typeName) {
        return getClassByDuckType(new DuckType(typeName));
    }

    /**
     * @throws JaversException TYPE_NAME_NOT_FOUND if given typeName is not registered
     * @since 1.4
     */
    Class getClassByDuckType(DuckType duckType) {
        argumentsAreNotNull(duckType);

        Class javaType = mappedTypeNames.get(duckType);
        if (javaType != null){
            return javaType;
        }

        synchronized (duckType.getTypeName()) {
            Optional<? extends Class> classForName = parseClass(duckType.getTypeName());
            if (classForName.isPresent()) {
                mappedTypeNames.put(duckType, classForName.get());
                return classForName.get();
            }
        }

        //try to fallback to bare typeName when properties doesn't match
        if (!duckType.isBare()){
            return getClassByDuckType(duckType.bareCopy());
        }

        throw new JaversException(JaversExceptionCode.TYPE_NAME_NOT_FOUND, duckType.getTypeName());
    }

    boolean contains(Type javaType){
        return mappedTypes.get(javaType) != null;
    }

    JaversType getJaversType(Type javaType) {
        argumentIsNotNull(javaType);

        if (javaType == Object.class) {
            return OBJECT_TYPE;
        }

        JaversType jType = mappedTypes.get(javaType);
        if (jType != null) {
            return jType;
        }

        return computeIfAbsent(javaType, new Function<Type, JaversType>() {
            public JaversType apply(Type type) {
                return infer(type);
            }
        });
    }

    void putIfAbsent(Type javaType, final JaversType jType) {
        computeIfAbsent(javaType, new Function<Type, JaversType>() {
            public JaversType apply(Type ignored) {
                return jType;
            }
        });
    }

    void computeIfAbsent(final ClientsClassDefinition def) {
        computeIfAbsent(def.getBaseJavaClass(), new Function<Type, JaversType>() {
            public JaversType apply(Type ignored) {
                return typeFactory.create(def);
            }
        });
    }

    //synchronizes on map Key (javaType) only for map writes
    private JaversType computeIfAbsent(Type javaType, Function<Type, JaversType> computeFunction) {

        synchronized (javaType) {
            //map.contains double check
            JaversType mappedType = mappedTypes.get(javaType);
            if (mappedType != null) {
                return mappedType;
            }

            JaversType newType = computeFunction.apply(javaType);

            addFullMapping(javaType, newType);

            inferIdPropertyTypeForEntityIfNeed(newType);

            return newType;
        }
    }

    /**
     * if type of given id-property is not already mapped, maps it as ValueType
     * <p/>
     * must be called within synchronized block
     */
    private void inferIdPropertyTypeForEntityIfNeed(JaversType jType) {
        argumentIsNotNull(jType);
        if (jType instanceof EntityType) {
            EntityType entityType = (EntityType) jType;
            Type idType = entityType.getIdPropertyGenericType();
            addFullMapping(idType, typeFactory.inferIdPropertyTypeAsValue(idType));
        }
    }

    /**
     * must be called within synchronized block
     */
    private void addFullMapping(Type javaType, JaversType newType){
        mappedTypes.put(javaType, newType);

        if (newType instanceof ManagedType){
            ManagedType managedType = (ManagedType)newType;
            mappedTypeNames.put(new DuckType(managedType.getName()), ReflectionUtil.extractClass(javaType));
            mappedTypeNames.put(new DuckType(managedType), ReflectionUtil.extractClass(javaType));
        }
    }

    /**
     * must be called within synchronized block
     */
    private JaversType infer(Type javaType) {
        argumentIsNotNull(javaType);
        JaversType prototype = findNearestAncestor(javaType);
        JaversType newType = typeFactory.infer(javaType, Optional.fromNullable(prototype));

        return newType;
    }

    private JaversType findNearestAncestor(Type javaType) {
        Class javaClass = extractClass(javaType);
        List<DistancePair> distances = new ArrayList<>();

        for (JaversType javersType : mappedTypes.values()) {
            DistancePair distancePair = new DistancePair(javaClass, javersType);

            //this is due too spoiled Java Array reflection API
            if (javaClass.isArray()) {
                return getJaversType(Object[].class);
            }

            //just to better speed
            if (distancePair.getDistance() == 0) {
                return distancePair.getJaversType();
            }

            distances.add(distancePair);
        }

        Collections.sort(distances);

        if (distances.get(0).isMax()) {
            return null;
        }

        return distances.get(0).getJaversType();
    }

    private Optional<? extends Class> parseClass(String qualifiedName){
        try {
            return Optional.of( TypeMapperState.class.forName(qualifiedName) );
        }
        catch (ClassNotFoundException e){
            return Optional.empty();
        }
    }
}
