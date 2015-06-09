package org.javers.core.metamodel.type;

import org.javers.common.string.PrettyPrintBuilder;
import org.javers.common.string.ToStringBuilder;
import org.javers.core.metamodel.clazz.Entity;
import org.javers.core.metamodel.clazz.EntityDefinition;
import org.javers.core.metamodel.clazz.ManagedClassFactory;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.property.Property;

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
        return new EntityType(managedClassFactory.create(new EntityDefinition(javaType, getManagedClass().getIdProperty())));
    }

    public Type getIdPropertyGenericType() {
        return getManagedClass().getIdProperty().getGenericType();
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this,
                "baseType", getBaseJavaType(),
                "id", getIdProperty().getName());
    }

    @Override
    protected PrettyPrintBuilder prettyPrintBuilder() {
        return super.prettyPrintBuilder().addField("idProperty", getIdProperty().getName());
    }

    public Property getIdProperty() {
        return getManagedClass().getIdProperty();
    }
}
