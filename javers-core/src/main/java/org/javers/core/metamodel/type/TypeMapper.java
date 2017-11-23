package org.javers.core.metamodel.type;

import org.javers.common.collections.Primitives;
import org.javers.common.collections.WellKnownValueTypes;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Maps Java types into Javers types
 *
 * @author bartosz walacik
 */
public class TypeMapper {
    private static final Logger logger = LoggerFactory.getLogger(TypeMapper.class);

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

        registerPrimitiveType(Enum.class);

        //array
        addType(new ArrayType(Object[].class));

        //well known Value types
        for (Class valueType : WellKnownValueTypes.getValueTypes()) {
            registerValueType(valueType);
        }

        //Collections
        addType(new CollectionType(Collection.class)); //only for exception handling
        addType(new SetType(Set.class));
        addType(new ListType(List.class));
        addType(new OptionalType());

        //& Maps
        addType(new MapType(Map.class));
    }

    public MapContentType getMapContentType(KeyValueType mapType){
        JaversType keyType = getJaversType(mapType.getKeyType());
        JaversType valueType = getJaversType(mapType.getValueType());
        return new MapContentType(keyType, valueType);
    }

    /**
     * only for change appenders
     */
    public MapContentType getMapContentType(ContainerType containerType){
        JaversType keyType = getJaversType(Integer.class);
        JaversType valueType = getJaversType(containerType.getItemType());
        return new MapContentType(keyType, valueType);
    }

    /**
     * is Set, List or Array of ManagedClasses
     */
    public boolean isContainerOfManagedTypes(JaversType javersType){
        if (! (javersType instanceof ContainerType)) {
            return false;
        }

        return getJaversType(((ContainerType)javersType).getItemType()) instanceof ManagedType;
    }

    /**
     * is Map (or Multimap) with ManagedClass on Key or Value position
     */
    public boolean isKeyValueTypeWithManagedTypes(JaversType enumerableType) {
        if (enumerableType instanceof KeyValueType){
            KeyValueType mapType = (KeyValueType)enumerableType;

            JaversType keyType = getJaversType(mapType.getKeyType());
            JaversType valueType = getJaversType(mapType.getValueType());

            return keyType instanceof ManagedType || valueType instanceof ManagedType;
        } else{
            return false;
        }
    }

    /**
     * Returns mapped type, spawns a new one from a prototype,
     * or infers a new one using default mapping.
     */
    public JaversType getJaversType(Type javaType) {
        argumentIsNotNull(javaType);
        return state.getJaversType(javaType);
    }

    public ClassType getJaversClassType(Type javaType) {
        argumentIsNotNull(javaType);
        JaversType jType = getJaversType(javaType);

        if (jType instanceof ClassType) {
            return (ClassType) jType;
        }

        throw new JaversException(JaversExceptionCode.CLASS_MAPPING_ERROR,
                    javaType,
                    jType.getClass().getSimpleName(),
                    ClassType.class.getSimpleName());
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
        try {
            return (T) getJaversType(property.getGenericType());
        }catch (JaversException e) {
            logger.error("Can't calculate JaversType for property: {}", property);
            throw e;
        }
    }

    private void registerPrimitiveType(Class<?> primitiveClass) {
        addType(new PrimitiveType(primitiveClass));
    }

    public void registerClientsClass(ClientsClassDefinition def) {
        state.register(def);
    }

    public void registerValueType(Class<?> valueCLass) {
        addType(new ValueType(valueCLass));
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

    public void addType(JaversType jType) {
        Validate.argumentIsNotNull(jType);
        state.putIfAbsent(jType.getBaseJavaType(), jType);
    }

    public void addTypes(Collection<JaversType> jTypes) {
        Validate.argumentIsNotNull(jTypes);
        for (JaversType t : jTypes) {
            addType(t);
        }
    }

    boolean contains(Type javaType){
        return state.contains(javaType);
    }
}
