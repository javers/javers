package org.javers.core.metamodel.type;

import org.javers.core.metamodel.clazz.Entity;
import org.javers.core.metamodel.clazz.ManagedClassFactory;
import org.javers.core.metamodel.object.InstanceId;

import java.lang.reflect.Type;

/**
 * Entity class in client's domain model.
 * <br/><br/>
 *
 * Has list of mutable properties and its own identity hold in idProperty.
 * <br/><br/>

 * Two Entity instances are compared using idProperty, see {@link InstanceId}
 * <br/><br/>
 *
 * Example:
 * <pre>
 *     class Person {
 *         @Id
 *         private int    personId;
 *         private String firstName;
 *         private String lastName;
 *         ...
 *     }
 * </pre>
 *
 * @author bartosz walacik
 */
public class EntityType extends ManagedType {

    public EntityType(Entity entity){
        super(entity);
    }

    @Override
    public Entity getManagedClass() {
        return (Entity)super.getManagedClass();
    }

    @Override
    ManagedType spawn(Class javaType, ManagedClassFactory managedClassFactory) {
        return new EntityType(managedClassFactory.createEntity(javaType));
    }

    public Type getIdPropertyGenericType() {
        return getManagedClass().getIdProperty().getGenericType();
    }
}
