package org.javers.core.metamodel.type;

import org.javers.core.metamodel.property.*;

import java.lang.reflect.Type;

import static org.javers.common.reflection.ReflectionUtil.extractClass;

/**
 * @author bartosz walacik
 */
public class TypeSpawningFactory {
    private ManagedClassFactory managedClassFactory;

    public TypeSpawningFactory(ManagedClassFactory managedClassFactory) {
        this.managedClassFactory = managedClassFactory;
    }

    ManagedType spawnFromDefinition(ManagedClassDefinition def){
        if (def instanceof ValueObjectDefinition) {
            ValueObject valueObject = managedClassFactory.create((ValueObjectDefinition) def);
            return new ValueObjectType(valueObject);
        }
        if (def instanceof EntityDefinition) {
            Entity entity = managedClassFactory.create((EntityDefinition)def);
            return new EntityType(entity);
        }
        throw new IllegalArgumentException("unsupported "+def);
    }

    JaversType spawnFromPrototype(JaversType prototype, Type javaType) {

        if (prototype instanceof ManagedType) {
            Class javaClass = extractClass(javaType);
            return ((ManagedType)prototype).spawn(javaClass, managedClassFactory);
        }
        else {
            return prototype.spawn(javaType); //delegate to simple constructor
        }
    }
}
