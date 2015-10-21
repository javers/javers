package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.string.PrettyPrintBuilder;
import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.EntityDefinition;
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
    @Deprecated
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

    /**
     * @param instance instance of {@link #getBaseJavaClass()}
     * @return returns ID of given instance (value of idProperty)
     * @throws JaversException ENTITY_INSTANCE_WITH_NULL_ID
     * @throws JaversException NOT_INSTANCE_OF
     */
    public Object getIdOf(Object instance) {
        Validate.argumentIsNotNull(instance);

        if (!getBaseJavaClass().isInstance(instance)) {
            throw new JaversException(JaversExceptionCode.NOT_INSTANCE_OF, getName(), instance.getClass().getName());
        }

        Object cdoId = getIdProperty().get(instance);
        if (cdoId == null) {
            throw new JaversException(JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID, getName());
        }
        return cdoId;
    }
}
