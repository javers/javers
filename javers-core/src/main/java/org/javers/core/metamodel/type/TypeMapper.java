package org.javers.core.metamodel.type;

import org.javers.common.collections.Primitives;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.scanner.ClassScanner;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

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
    private final TypeMapperState state;
    private final DehydratedTypeFactory dehydratedTypeFactory = new DehydratedTypeFactory(this);

    public TypeMapper(ClassScanner classScanner) {
        //Pico doesn't support cycles, so manual construction
        TypeFactory typeFactory = new TypeFactory(classScanner, this);
        this.state = new TypeMapperState(typeFactory);
        registerCoreTypes();
    }

    /**
     * for TypeMapperConcurrentTest only, no better idea how to writhe this test
     * without additional constructor
     */
    protected TypeMapper(TypeFactory typeFactory ) {
        this.state = new TypeMapperState(typeFactory);
        registerCoreTypes();
    }

    private void registerCoreTypes(){
        //primitives & boxes
        for (Class primitiveOrBox : Primitives.getPrimitiveAndBoxTypes()) {
            registerPrimitiveType(primitiveOrBox);
        }

        //String & Enum
        registerPrimitiveType(String.class);
        registerPrimitiveType(CharSequence.class);
        registerPrimitiveType(Enum.class);

        //array
        addType(new ArrayType(Object[].class));

        //well known Value types
        registerValueType(LocalDateTime.class);
        registerValueType(LocalDate.class);
        registerValueType(BigDecimal.class);
        registerValueType(Date.class);
        registerValueType(ThreadLocal.class);

        //Collections
        addType(new CollectionType(Collection.class)); //only for exception handling
        addType(new SetType(Set.class));
        addType(new ListType(List.class));

        //& Maps
        addType(new MapType(Map.class));

        // bootstrap phase 2: add-ons
        if (ReflectionUtil.isJava8runtime()){
            addType(new OptionalType());
        }
    }

    public MapContentType getMapContentType(MapType mapType){
        JaversType keyType = getJaversType(mapType.getKeyType());
        JaversType valueType = getJaversType(mapType.getValueType());
        return new MapContentType(keyType, valueType);
    }

    /**
     * for change appenders
     */
    public MapContentType getMapContentType(ContainerType containerType){
        JaversType keyType = getJaversType(Integer.class);
        JaversType valueType = getJaversType(containerType.getItemType());
        return new MapContentType(keyType, valueType);
    }

    /**
     * returns mapped type or spawns new one from prototype
     * or infers new one using default mapping
     */
    public JaversType getJaversType(Type javaType) {
        argumentIsNotNull(javaType);
        return state.getJaversType(javaType);
    }

    /**
     * @throws JaversException TYPE_NAME_NOT_FOUND if given typeName is not registered
     * @since 1.4
     */
    public ManagedType getJaversManagedType(String typeName){
        return getJaversManagedType(state.getClassByTypeName(typeName), ManagedType.class);
    }

    /**
     * @throws JaversException TYPE_NAME_NOT_FOUND if given typeName is not registered
     * @since 1.4
     */
    public ManagedType getJaversManagedType(GlobalId globalId){
        return getJaversManagedType(state.getClassByTypeName(globalId.getTypeName()), ManagedType.class);
    }

    /**
     * @throws JaversException TYPE_NAME_NOT_FOUND if given typeName is not registered
     * @since 1.4
     */
    public <T extends ManagedType> T getJaversManagedType(String typeName, Class<T> expectedType) {
        return getJaversManagedType(state.getClassByTypeName(typeName), expectedType);
    }

    /**
     * @throws JaversException TYPE_NAME_NOT_FOUND if given typeName is not registered
     * @since 1.4
     */
    public <T extends ManagedType> T getJaversManagedType(DuckType duckType, Class<T> expectedType) {
        return getJaversManagedType(state.getClassByDuckType(duckType), expectedType);
    }

    /**
     * If given javaClass is mapped to ManagedType, returns its JaversType
     *
     * @throws JaversException MANAGED_CLASS_MAPPING_ERROR
     */
    public ManagedType getJaversManagedType(Class javaType) {
        return getJaversManagedType(javaType, ManagedType.class);
    }

    /**
     * If given javaClass is mapped to expected ManagedType, returns its JaversType
     *
     * @throws JaversException MANAGED_CLASS_MAPPING_ERROR
     */
    public <T extends ManagedType> T getJaversManagedType(Class javaClass, Class<T> expectedType) {
        JaversType mType = getJaversType(javaClass);

        if (expectedType.isAssignableFrom(mType.getClass())) {
            return (T) mType;
        } else {
            throw new JaversException(JaversExceptionCode.MANAGED_CLASS_MAPPING_ERROR,
                    javaClass,
                    mType.getClass().getSimpleName(),
                    expectedType.getSimpleName());
        }
    }

    public <T extends JaversType> T getPropertyType(Property property){
        argumentIsNotNull(property);
        return (T) getJaversType(property.getGenericType());
    }

    private void registerPrimitiveType(Class<?> primitiveClass) {
        addType(new PrimitiveType(primitiveClass));
    }

    public void registerClientsClass(ClientsClassDefinition def) {
        state.computeIfAbsent(def);
    }

    public void registerValueType(Class<?> valueCLass) {
        addType(new ValueType(valueCLass));
    }

    public void registerCustomType(Class<?> customCLass) {
        addType(new CustomType(customCLass));
    }

    public boolean isValueObject(Type type) {
        JaversType jType  = getJaversType(type);
        return  jType instanceof ValueObjectType;
    }

    /**
     * Dehydrated type for JSON representation
     */
    public Type getDehydratedType(Type type) {
        return dehydratedTypeFactory.build(type);
    }

    private void addType(JaversType jType) {
        state.putIfAbsent(jType.getBaseJavaType(), jType);
    }

    boolean contains(Type javaType){
        return state.contains(javaType);
    }
}
