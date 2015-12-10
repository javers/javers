package org.javers.core.metamodel.type;

import org.javers.common.collections.Optional;
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
    private final Property idProperty;

    EntityType(ManagedClass entity, Optional<Property> idProperty, Optional<String> typeName) {
        super(entity, typeName);
        Validate.argumentIsNotNull(idProperty);

        if (idProperty.isEmpty()) {
            this.idProperty = findDefaultIdProperty();
        }
        else {
            this.idProperty = idProperty.get();
        }
    }

    EntityType(ManagedClass entity, Optional<Property> idProperty) {
        this(entity, idProperty, Optional.<String>empty());
    }

    @Override
    EntityType spawn(ManagedClass managedClass, Optional<String> typeName) {
        return new EntityType(managedClass, Optional.of(idProperty), typeName);
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

    public Property getIdProperty() {
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
            throw new JaversException(JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID, getName());
        }
        return cdoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || !(o instanceof EntityType)) {return false;}

        EntityType that = (EntityType) o;
        return super.equals(that) && idProperty.equals(that.idProperty);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + idProperty.hashCode();
    }

    /**
     * @throws JaversException ENTITY_WITHOUT_ID
     */
    private Property findDefaultIdProperty() {
        for (Property p : getProperties()) {
            if (p.looksLikeId()) {
                return p;
            }
        }
        throw new JaversException(JaversExceptionCode.ENTITY_WITHOUT_ID, getName());
    }
}
