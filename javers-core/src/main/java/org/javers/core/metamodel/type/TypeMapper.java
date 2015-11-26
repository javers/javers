package org.javers.core.metamodel.type;

import org.javers.common.collections.Primitives;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.Javers;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
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

    public TypeMapper(TypeFactory typeFactory) {
        this.state = new TypeMapperState(typeFactory);

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
        registerValueType(URI.class);
        registerValueType(URL.class);
        registerValueType(Path.class);


        //Collections
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
     * throws JaversException.MANAGED_CLASS_MAPPING_ERROR if given javaClass is NOT mapped to {@link ManagedType}
     */
    public ManagedType getJaversManagedType(Class javaType) {
        return getJaversManagedType(javaType, ManagedType.class);
    }

    /**
     * if given javaClass is mapped to expected JaversType, returns its JaversType,
     * otherwise throws JaversException.MANAGED_CLASS_MAPPING_ERROR
     */
    public <T extends ManagedType> T getJaversManagedType(Class javaClass, Class<T> expectedType) {
        JaversType mType = getJaversType(javaClass);

        if (expectedType.isAssignableFrom(mType.getClass())) {
            return (T) mType;
        } else {
            throw new JaversException(JaversExceptionCode.MANAGED_CLASS_MAPPING_ERROR,
                    javaClass,
                    mType.getName(),
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

    public ValueObjectType getChildValueObject(EntityType owner, String voPropertyName) {
        JaversType javersType = getJaversType(owner.getProperty(voPropertyName).getGenericType());

        if (javersType instanceof ValueObjectType) {
            return (ValueObjectType) javersType;
        }
        
        if (javersType instanceof ContainerType) {
            JaversType contentType  = getJaversType(((ContainerType) javersType).getItemType());
            if (contentType instanceof ValueObjectType){
                return (ValueObjectType)contentType;
            }
        } 
        
        if (javersType instanceof MapType){
            JaversType valueType  = getJaversType(((MapType) javersType).getValueType());
            if (valueType instanceof ValueObjectType){
                return (ValueObjectType)valueType;
            } 
        }

        throw new JaversException(JaversExceptionCode.CANT_EXTRACT_CHILD_VALUE_OBJECT,
                  owner.getName()+"."+voPropertyName,
                  javersType);

    }

    //-- private

    private void addType(JaversType jType) {
        state.putIfAbsent(jType.getBaseJavaType(), jType);
    }

}
