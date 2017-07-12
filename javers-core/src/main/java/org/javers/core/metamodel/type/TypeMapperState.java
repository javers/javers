package org.javers.core.metamodel.type;

import java.util.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import static org.javers.common.reflection.ReflectionUtil.extractClass;
import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * thread-safe, thin wrapper for map
 *
 * @author bartosz.walacik
 */
class TypeMapperState {
    private final Map<String, JaversType> mappedTypes = new ConcurrentHashMap<>();
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
        return mappedTypes.containsKey(javaType.toString());
    }

    JaversType getJaversType(Type javaType) {
        argumentIsNotNull(javaType);

        if (javaType == Object.class) {
            return OBJECT_TYPE;
        }

        JaversType jType = getFromMap(javaType);
        if (jType != null) {
            return jType;
        }

        return computeIfAbsent(javaType, type -> infer(type));
    }

    void putIfAbsent(Type javaType, final JaversType jType) {
        computeIfAbsent(javaType, ignored -> jType);
    }

    void register(final ClientsClassDefinition def) {
        Type javaType = def.getBaseJavaClass();
        JaversType newType = typeFactory.create(def);

        addFullMapping(javaType, newType);
    }

    //synchronizes on map Key (javaType) only for map writes
    private JaversType computeIfAbsent(Type javaType, Function<Type, JaversType> computeFunction) {

        synchronized (javaType) {
            //map.contains double check
            JaversType mappedType = getFromMap(javaType);
            if (mappedType != null) {
                return mappedType;
            }

            JaversType newType = computeFunction.apply(javaType);

            addFullMapping(javaType, newType);

            return newType;
        }
    }

    private void addFullMapping(Type javaType, JaversType newType){
        Validate.argumentsAreNotNull(javaType, newType);

        putToMap(javaType, newType);

        if (newType instanceof ManagedType){
            ManagedType managedType = (ManagedType)newType;
            mappedTypeNames.put(new DuckType(managedType.getName()), ReflectionUtil.extractClass(javaType));
            mappedTypeNames.put(new DuckType(managedType), ReflectionUtil.extractClass(javaType));
        }

        if (newType instanceof EntityType) {
            inferIdPropertyTypeForEntity((EntityType) newType);
        }
    }

    /**
     * maps type of given Entity's id-property as ValueType
     */
    private void inferIdPropertyTypeForEntity(EntityType entityType) {
        Type idType = entityType.getIdPropertyGenericType();
        addFullMapping(idType, typeFactory.inferIdPropertyTypeAsValue(idType));
    }

    /**
     * must be called within synchronized block
     */
    private JaversType infer(Type javaType) {
        argumentIsNotNull(javaType);
        JaversType prototype = findNearestAncestor(javaType);
        JaversType newType = typeFactory.infer(javaType, Optional.ofNullable(prototype));

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

    private JaversType getFromMap(Type javaType) {
        return mappedTypes.get(javaType.toString());
    }

    private void putToMap(Type javaType, JaversType javersType) {
        mappedTypes.put(javaType.toString(), javersType);
    }
}
