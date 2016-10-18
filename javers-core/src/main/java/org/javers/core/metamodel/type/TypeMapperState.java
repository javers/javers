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

    JaversType getJaversType(Type javaType, final boolean asShallowReference) {
        argumentIsNotNull(javaType);

        if (javaType == Object.class) {
            return OBJECT_TYPE;
        }

        if (asShallowReference) {
            synchronized (javaType) {
                return infer(javaType, true);
            }
        } else {
            JaversType jType = getFromMap(javaType);
            if (jType != null) {
                return jType;
            }

            return computeIfAbsent(javaType, new Function<Type, JaversType>() {
                public JaversType apply(Type type) {
                    return infer(type, false);
                }
            });
        }
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
            JaversType mappedType = getFromMap(javaType);
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
        putToMap(javaType, newType);

        if (newType instanceof ManagedType){
            ManagedType managedType = (ManagedType)newType;
            mappedTypeNames.put(new DuckType(managedType.getName()), ReflectionUtil.extractClass(javaType));
            mappedTypeNames.put(new DuckType(managedType), ReflectionUtil.extractClass(javaType));
        }
    }

    /**
     * must be called within synchronized block
     */
    private JaversType infer(Type javaType, boolean asShallowReference) {
        argumentIsNotNull(javaType);

        if (asShallowReference) {
            return typeFactory.inferFromAnnotations(javaType, true);
        } else {
            Optional<JaversType> prototype = findNearestAncestor(javaType);
            return prototype.isPresent() ?
                typeFactory.spawnFromPrototype(javaType, prototype.get()) :
                typeFactory.inferFromAnnotations(javaType, false);
        }
    }

    private Optional<JaversType> findNearestAncestor(Type javaType) {
        Class javaClass = extractClass(javaType);
        List<DistancePair> distances = new ArrayList<>();

        for (JaversType javersType : mappedTypes.values()) {
            DistancePair distancePair = new DistancePair(javaClass, javersType);

            //this is due too spoiled Java Array reflection API
            if (javaClass.isArray()) {
                return Optional.of(getJaversType(Object[].class, false));
            }

            //just to better speed
            if (distancePair.getDistance() == 0) {
                return Optional.of(distancePair.getJaversType());
            }

            distances.add(distancePair);
        }

        Collections.sort(distances);

        if (distances.get(0).isMax()) {
            return Optional.empty();
        }

        return Optional.of(distances.get(0).getJaversType());
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
