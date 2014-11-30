package org.javers.core.metamodel.type;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.*;
import org.slf4j.Logger;

import java.lang.reflect.Type;

import static org.javers.common.reflection.ReflectionUtil.extractClass;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author bartosz walacik
 */
public class TypeFactory {
    private static final Logger logger = getLogger(TypeFactory.class);

    private final ManagedClassFactory managedClassFactory;

    public TypeFactory(ManagedClassFactory managedClassFactory) {
        this.managedClassFactory = managedClassFactory;
    }

    JaversType createFromDefinition(ClientsClassDefinition def){
        return createFromClientsClass(managedClassFactory.create(def));
    }

    JaversType infer(Type javaType, Optional<JaversType> prototype){
        JaversType jType;

        if (prototype.isPresent()) {
            jType = spawnFromPrototype(javaType, prototype.get());
            logger.info("javersType of [{}] inferred as {} from prototype {}",
                        javaType, jType.getClass().getSimpleName(), prototype.get());
        }
        else {
            jType = inferFromAnnotations(javaType);
            logger.info("javersType of [{}] inferred as {} from annotations",
                        javaType, jType.getClass().getSimpleName());
        }

        return jType;
    }

    JaversType inferIdPropertyTypeAsValue(EntityType eType) {
        Type idPropertyType = eType.getIdPropertyGenericType();

        logger.info("javersType of [{}] inferred as ValueType, it's used as id-property type in {}",
                idPropertyType, eType);

        return new ValueType(idPropertyType);
    }

    private JaversType spawnFromPrototype(Type javaType, JaversType prototype) {
        Validate.argumentsAreNotNull(javaType, prototype);
        Class javaClass = extractClass(javaType);

        if (prototype instanceof ManagedType) {
            return ((ManagedType)prototype).spawn(javaClass, managedClassFactory);
        }
        else {
            return prototype.spawn(javaType); //delegate to simple constructor
        }
    }

    private JaversType inferFromAnnotations(Type javaType) {
        Class javaClass = extractClass(javaType);

        return createFromClientsClass(managedClassFactory.inferFromAnnotations(javaClass));
    }

    private JaversType createFromClientsClass(ClientsDomainClass clientsClass) {
        if (clientsClass instanceof Value) {
            return new ValueType(clientsClass.getClientsClass());
        }
        if (clientsClass instanceof ValueObject) {
            return new ValueObjectType((ValueObject)clientsClass);
        }
        if (clientsClass instanceof Entity) {
            return new EntityType((Entity)clientsClass);
        }
        throw new IllegalArgumentException("unsupported "+clientsClass.getName());
    }
}
