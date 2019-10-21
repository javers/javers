package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.common.validation.Validate;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.metamodel.annotation.Value;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.javers.common.reflection.ReflectionUtil.extractClass;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Maps Java types into Javers types
 *
 * @author bartosz walacik
 */
public class TypeMapper {
    static final Logger logger = LoggerFactory.getLogger("org.javers.TypeMapper");
    static final ValueType OBJECT_TYPE = new ValueType(Object.class);

    private final TypeMapperEngine engine = new TypeMapperEngine();
    private final TypeFactory typeFactory;

    private final DehydratedTypeFactory dehydratedTypeFactory = new DehydratedTypeFactory(this);

    public TypeMapper(ClassScanner classScanner, JaversCoreConfiguration javersCoreConfiguration) {
        //Pico doesn't support cycles, so manual construction
        TypeFactory typeFactory = new TypeFactory(classScanner, this);

        engine.registerCoreTypes(javersCoreConfiguration.getListCompareAlgorithm());
        this.typeFactory = typeFactory;
    }

    /**
     * For TypeMapperConcurrentTest only,
     * no better idea how to writhe this test without additional constructor
     */
    @Deprecated
    protected TypeMapper(TypeFactory typeFactory) {
        this.typeFactory = typeFactory;
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

    public JaversType getContainerItemType(JaversProperty property) {
        ContainerType containerType = property.getType();
        return getJaversType(containerType.getItemType());
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

    public boolean isManagedType(JaversType javersType){
        return javersType instanceof ManagedType;
    }

    public boolean isEnumerableOfManagedTypes(JaversType javersType){
        return isContainerOfManagedTypes(javersType) || isKeyValueTypeWithManagedTypes(javersType);
    }

    /**
     * Returns mapped type, spawns a new one from a prototype,
     * or infers a new one using default mapping.
     */
    public JaversType getJaversType(Type javaType) {
        argumentIsNotNull(javaType);

        if (javaType == Object.class) {
            return OBJECT_TYPE;
        }

        return engine.computeIfAbsent(javaType, j -> typeFactory.infer(j, findPrototype(j)));
    }

    public boolean isShallowReferenceType(Type javaType) {
        return getJaversType(javaType) instanceof ShallowReferenceType;
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
     */
    public ManagedType getJaversManagedType(GlobalId globalId){
        return getJaversManagedType(engine.getClassByTypeName(globalId.getTypeName()), ManagedType.class);
    }

    /**
     * @throws JaversException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    public <T extends ManagedType> T getJaversManagedType(String typeName, Class<T> expectedType) {
        return getJaversManagedType(engine.getClassByTypeName(typeName), expectedType);
    }

    /**
     * for tests only
     */
    private <T extends ManagedType> T getJaversManagedType(String typeName) {
        return (T)getJaversManagedType(engine.getClassByTypeName(typeName), ManagedType.class);
    }

    /**
     * @throws JaversException TYPE_NAME_NOT_FOUND if given typeName is not registered
     */
    public <T extends ManagedType> T getJaversManagedType(DuckType duckType, Class<T> expectedType) {
        return getJaversManagedType(engine.getClassByDuckType(duckType), expectedType);
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

    public <T extends ManagedType> Optional<T> getJaversManagedTypeMaybe(String typeName, Class<T> expectedType) {
        return getJaversManagedTypeMaybe(new DuckType(typeName), expectedType);
    }

    public <T extends ManagedType> Optional<T> getJaversManagedTypeMaybe(DuckType duckType, Class<T> expectedType) {
        try {
            return Optional.of(getJaversManagedType(duckType, expectedType));
        } catch (JaversException e) {
            if (JaversExceptionCode.TYPE_NAME_NOT_FOUND == e.getCode()) {
                return Optional.empty();
            }
            if (JaversExceptionCode.MANAGED_CLASS_MAPPING_ERROR == e.getCode()) {
                return Optional.empty();
            }
            throw e;
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

    public void registerClientsClass(ClientsClassDefinition def) {
        JaversType newType = typeFactory.create(def);

        logger.debug("javersType of '{}' " + "mapped explicitly to {}",
                def.getBaseJavaClass().getSimpleName(), newType.getClass().getSimpleName());

        engine.registerExplicitType(newType);
    }

    /**
     * Dehydrated type for JSON representation
     */
    public Type getDehydratedType(Type type) {
        return dehydratedTypeFactory.build(type);
    }

    public void addPluginTypes(Collection<JaversType> jTypes) {
        Validate.argumentIsNotNull(jTypes);
        for (JaversType t : jTypes) {
            engine.registerExplicitType(t);
        }
    }

    boolean contains(Type javaType){
        return engine.contains(javaType);
    }

    private Optional<JaversType> findPrototype(Type javaType) {
        if (javaType instanceof TypeVariable) {
            return Optional.empty();
        }

        Class javaClass = extractClass(javaType);

        //this is due too spoiled Java Array reflection API
        if (javaClass.isArray()) {
            return Optional.of(getJaversType(Object[].class));
        }

        JaversType selfClassType = engine.get(javaClass);
        if (selfClassType != null && javaClass != javaType){
            return  Optional.of(selfClassType); //returns rawType for ParametrizedTypes
        }

        List<Type> hierarchy = ReflectionUtil.calculateHierarchyDistance(javaClass);

        for (Type parent : hierarchy) {
            JaversType jType = engine.get(parent);
            if (jType != null && jType.canBePrototype()) {
                logger.debug("proto for {} -> {}", javaType, jType);
                return Optional.of(jType);
            }
        }

        return Optional.empty();
    }
}
