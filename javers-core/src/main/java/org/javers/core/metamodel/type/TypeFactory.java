package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.*;

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

    JaversType createFromDefinition(ManagedClassDefinition def){
        if (def instanceof  ValueDefinition){
            return new ValueType(def.getClazz());
        }
        else {
            ManagedClass managedClass = managedClassFactory.create(def);
            return createFromManagedClass(managedClass);
        }
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

    public JaversType infer(Type javaType) {
        Class javaClass = extractClass(javaType);

        ManagedClass managedClass = managedClassFactory.infer(javaClass);

        return createFromManagedClass(managedClass);
    }

    private JaversType createFromManagedClass(ManagedClass managedClass) {
        if (managedClass instanceof ValueObject) {
            return new ValueObjectType((ValueObject)managedClass);
        }
        if (managedClass instanceof Entity) {
            return new EntityType((Entity)managedClass);
        }
        throw new IllegalArgumentException("unsupported "+managedClass);
    }
}
