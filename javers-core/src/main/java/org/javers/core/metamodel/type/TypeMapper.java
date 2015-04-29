package org.javers.core.metamodel.type;

import org.javers.common.collections.Optional;
import org.javers.common.collections.Primitives;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
import org.javers.core.metamodel.clazz.Entity;
import org.javers.core.metamodel.clazz.ManagedClass;
import org.javers.core.metamodel.clazz.ValueObject;
import org.javers.core.metamodel.property.Property;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
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
        registerValueType(ThreadLocal.class);
        registerValueType(URI.class);
        registerValueType(URL.class);
        registerValueType(Path.class);


        //Collections
        addType(new SetType(Set.class));
        addType(new ListType(List.class));

        //& Maps
        addType(new MapType(Map.class));
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

        if (javaType == Object.class){
            return OBJECT_TYPE;
        }

        JaversType jType = getExactMatchingJaversType(javaType);
        if (jType != null) {
            return jType;
        }

        return infer(javaType);
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
    public boolean isContainerOfManagedClasses(JaversType javersType){
        if (! (javersType instanceof ContainerType)) {
            return false;
        }

        return getJaversType(((ContainerType) javersType).getItemType()) instanceof ManagedType;
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

        JaversType keyType = getJaversType(mapType.getKeyType());
        JaversType valueType = getJaversType(mapType.getValueType());

        return keyType instanceof ManagedType || valueType instanceof ManagedType;
    }

    private void registerPrimitiveType(Class<?> primitiveClass) {
        addType(new PrimitiveType(primitiveClass));
    }

    public void registerClientsClass(ClientsClassDefinition def) {
        addType(typeFactory.createFromDefinition(def));
    }

    public void registerValueType(Class<?> valueCLass) {
        addType(new ValueType(valueCLass));
    }

    public void registerCustomType(Class<?> customCLass) {
        addType(new CustomType(customCLass));
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

    public boolean isValueObject(Type type) {
        JaversType jType  = getJaversType(type);
        return  jType instanceof ValueObjectType;
    }

    public Type getDehydratedType(Type type){
        final JaversType javersType = getJaversType(type);

        if (!javersType.isGenericType()){
            return javersType.getRawDehydratedType();
        }
        return new ParametrizedDehydratedType(javersType, this);
    }

    /**
     * if given javaClass is mapped to {@link ManagedType}
     * returns {@link ManagedType#getManagedClass()}
     *
     * @throws JaversException MANAGED_CLASS_MAPPING_ERROR
     */
    public <T extends ManagedClass> T getManagedClass(Class javaClass, Class<T> expectedType) {
        ManagedType mType = getJaversManagedType(javaClass);

        if (mType.getManagedClass().getClass().equals( expectedType)) {
            return (T)mType.getManagedClass();
        }
        else {
            throw new JaversException(JaversExceptionCode.MANAGED_CLASS_MAPPING_ERROR,
                    javaClass,
                    mType.getManagedClass().getSimpleName(),
                    expectedType.getSimpleName());
        }
    }

    public ValueObject getChildValueObject(Entity owner, String voPropertyName) {
        JaversType javersType = getJaversType( owner.getProperty(voPropertyName).getGenericType() );

        if (javersType instanceof ValueObjectType) {
            return ((ValueObjectType) javersType).getManagedClass();
        }

        if (javersType instanceof ContainerType) {
            JaversType contentType  = getJaversType(((ContainerType) javersType).getItemType());
            if (contentType instanceof ValueObjectType){
                return ((ValueObjectType)contentType).getManagedClass();
            }
        }

        throw new JaversException(JaversExceptionCode.CANT_EXTRACT_CHILD_VALUE_OBJECT,
                  owner.getName()+"."+voPropertyName,
                  javersType);

    }

    //-- private

    /**
     * is ContainerType (Set, List or Array) of ManagedClasses
     *
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED if property type is not fully parametrized
     */
    private boolean isContainerOfValueObjects(JaversType javersType){
        if (! (javersType instanceof ContainerType)) {
            return false;
        }
        return getJaversType(((ContainerType) javersType).getItemType()) instanceof ValueObjectType;
    }

    private void addType(JaversType jType) {
        mappedTypes.put(jType.getBaseJavaType(), jType);

        if (jType instanceof EntityType){
            inferIdPropertyTypeAsValue((EntityType) jType);
        }
    }

    /**
     * @return null if not found
     */
    private JaversType getExactMatchingJaversType(Type javaType) {
        return mappedTypes.get(javaType);
    }

    private JaversType infer(Type javaType) {
        argumentIsNotNull(javaType);
        JaversType prototype = findNearestAncestor(javaType);
        JaversType newType = typeFactory.infer(javaType, Optional.fromNullable(prototype));

        addType(newType);
        return newType;
    }

    /**
     * if type of id-property is not already mapped, maps it as ValueType
     */
    private void inferIdPropertyTypeAsValue(EntityType eType) {
        argumentIsNotNull(eType);

        if (!isMapped(eType.getIdPropertyGenericType())) {
            addType(typeFactory.inferIdPropertyTypeAsValue(eType));;
        }
    }

    private boolean isMapped(Type javaType) {
        return mappedTypes.containsKey(javaType);
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
