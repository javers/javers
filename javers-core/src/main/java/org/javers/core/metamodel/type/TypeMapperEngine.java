package org.javers.core.metamodel.type;

import org.javers.common.collections.Primitives;
import org.javers.common.collections.WellKnownValueTypes;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.common.validation.Validate;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
import org.javers.java8support.Java8TypeAdapters;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * thread-safe, mutable state of JaversTypes mapping
 */
class TypeMapperEngine {
    static final Logger logger = TypeMapper.logger;

    private final Map<String, JaversType> mappedTypes = new ConcurrentHashMap<>();
    private final Map<DuckType, Class> mappedTypeNames = new ConcurrentHashMap<>();
    private final TypeMapperLazy typeMapperlazy;


    TypeMapperEngine(TypeMapperLazy typeMapperlazy) {
        this.typeMapperlazy = typeMapperlazy;
    }

    private void putIfAbsent(Type javaType, final JaversType jType) {
        Validate.argumentsAreNotNull(javaType, jType);
        if (contains(javaType)) {
            return;
        }

        addFullMapping(javaType, jType);
    }

    void registerCoreTypes(ListCompareAlgorithm listCompareAlgorithm,
                           Collection<ClientsClassDefinition> classesToSkip){
        List<JaversType> coreTypes = new ArrayList<>();
        Set<Class<?>> classesToSkipAsSet = classesToSkip.stream().map(it -> it.getBaseJavaClass())
                .collect(Collectors.toSet());

        //primitives & boxes
        Primitives.getPrimitiveAndBoxTypes()
                .forEach(primitiveOrBox -> coreTypes.add(new PrimitiveType(primitiveOrBox)));

        coreTypes.add(new PrimitiveType(Enum.class));

        //array
        coreTypes.add(new ArrayType(Object[].class, typeMapperlazy));

        //well known Value types
        WellKnownValueTypes.getOldGoodValueTypes()
                .forEach(valueType-> coreTypes.add(new ValueType(valueType)));

        //java util and sql types
        coreTypes.addAll(UtilTypeCoreAdapters.valueTypes());

        //java time types
        coreTypes.addAll(Java8TypeAdapters.valueTypes());

        //Collections
        coreTypes.add(new CollectionType(Collection.class, typeMapperlazy));
        coreTypes.add(new SetType(Set.class, typeMapperlazy));
        if (listCompareAlgorithm == ListCompareAlgorithm.AS_SET) {
            coreTypes.add(new ListAsSetType(List.class, typeMapperlazy));
        } else {
            coreTypes.add(new ListType(List.class, typeMapperlazy));
        }
        coreTypes.add(new OptionalType(typeMapperlazy));

        //& Maps
        coreTypes.add(new MapType(Map.class, typeMapperlazy));

        coreTypes.stream().filter(it -> !classesToSkipAsSet.contains(it.getBaseJavaType()))
            .forEach(it -> registerCoreType(it));
    }

    void registerExplicitType(JaversType javersType) {
        putIfAbsent(javersType.getBaseJavaType(), javersType);
    }

    private void registerCoreType(JaversType jType) {
        logger.debug("registering coreType: {} -> {} ", jType.getBaseJavaType().getTypeName(), jType.getClass().getSimpleName());
        putIfAbsent(jType.getBaseJavaType(), jType);
    }

    JaversType computeIfAbsent(Type javaType, Function<Type, JaversType> computeFunction) {
        JaversType javersType = get(javaType);
        if (javersType != null) {
            return javersType;
        }

        synchronized (javaType) {
            //map.contains double check
            JaversType mappedType = get(javaType);
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

        JaversType existingType = mappedTypes.put(javaType.toString(), newType);
        if (existingType != null) {
            throw new JaversException(JaversExceptionCode.ATTEMPT_TO_OVERWRITE_EXISTING_JAVERSTYPE_MAPPING, javaType.toString(), existingType, newType);
        }

        if (newType instanceof ManagedType){
            ManagedType managedType = (ManagedType)newType;
            mappedTypeNames.put(new DuckType(managedType.getName()), ReflectionUtil.extractClass(javaType));
            mappedTypeNames.put(new DuckType(managedType), ReflectionUtil.extractClass(javaType));
        }
    }

    JaversType get(Type javaType) {
        return mappedTypes.get(javaType.toString());
    }

    boolean contains(Type javaType) {
        return get(javaType) != null;
    }

    /**
     * @throws JaversException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    Class getClassByTypeName(String typeName) {
        return getClassByDuckType(new DuckType(typeName));
    }

    /**
     * @throws JaversException TYPE_NAME_NOT_FOUND if given typeName is not registered
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

    private Optional<? extends Class> parseClass(String qualifiedName){
        try {
            return Optional.of( this.getClass().forName(qualifiedName) );
        }
        catch (ClassNotFoundException e){
            return Optional.empty();
        }
    }

}
