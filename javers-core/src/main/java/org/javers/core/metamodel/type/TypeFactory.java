package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.*;

import java.lang.reflect.Type;

import static org.javers.common.reflection.ReflectionUtil.extractClass;

/**
 * @author bartosz walacik
 */
public class TypeFactory {
    private final ManagedClassFactory managedClassFactory;

    public TypeFactory(ManagedClassFactory managedClassFactory) {
        this.managedClassFactory = managedClassFactory;
    }

    JaversType createFromDefinition(ClientsClassDefinition def){
        return createFromClientsClass(managedClassFactory.create(def));
    }

    JaversType spawnFromPrototype(Type javaType, JaversType prototype) {
        Validate.argumentsAreNotNull(javaType, prototype);
        Class javaClass = extractClass(javaType);

        if (prototype instanceof ManagedType) {
            return ((ManagedType)prototype).spawn(javaClass, managedClassFactory);
        }
        else {
            return prototype.spawn(javaType); //delegate to simple constructor
        }
    }

    public JaversType inferFromAnnotations(Type javaType) {
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
