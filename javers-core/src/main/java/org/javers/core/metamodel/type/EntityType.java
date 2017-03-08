package org.javers.core.metamodel.type;

import java.util.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.string.PrettyPrintBuilder;
import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
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
 *        {@literal @}Id
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
    private final JaversProperty idProperty;

    EntityType(ManagedClass entity, JaversProperty idProperty, Optional<String> typeName) {
        super(entity, typeName);
        Validate.argumentIsNotNull(idProperty);
        this.idProperty = idProperty;
    }

    EntityType(ManagedClass entity, JaversProperty idProperty) {
        this(entity, idProperty, Optional.empty());
    }

    @Override
    EntityType spawn(ManagedClass managedClass, Optional<String> typeName) {
        return new EntityType(managedClass, idProperty, typeName);
    }

    public Type getIdPropertyGenericType() {
        return getIdProperty().getGenericType();
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

    public JaversProperty getIdProperty() {
        return idProperty;
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
            throw new JaversException(JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID, getName(), getIdProperty().getName());
        }
        return cdoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof EntityType)) {return false;}

        EntityType that = (EntityType) o;
        return super.equals(that) && idProperty.equals(that.idProperty);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + idProperty.hashCode();
    }
}
