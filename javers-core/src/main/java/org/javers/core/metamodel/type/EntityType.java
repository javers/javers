package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.string.PrettyPrintBuilder;
import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.InstanceId;

import java.lang.reflect.Type;
import java.util.Optional;

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

    @Override
    EntityType spawn(ManagedClass managedClass, Optional<String> typeName) {
        //when spawning from prototype, prototype.idProperty and child.idProperty are different objects
        //with (possibly) different return types, so we need to update Id pointer
        return new EntityType(managedClass, managedClass.getProperty(idProperty.getName()), typeName);
    }

    public Type getIdPropertyGenericType() {
        return getIdProperty().getGenericType();
    }

    public JaversProperty getIdProperty() {
        return idProperty;
    }

    private String getIdPropertyName() {
        return getIdProperty().getName();
    }

    public boolean isIdProperty(JaversProperty property) {
        return idProperty.equals(property);
    }

    /**
     * @param instance instance of {@link #getBaseJavaClass()}
     * @return returns ID of given instance (value of idProperty)
     */
    public Object getIdOf(Object instance) {
        Validate.argumentIsNotNull(instance);

        if (!isInstance(instance)) {
            throw new JaversException(JaversExceptionCode.NOT_INSTANCE_OF,
                    getName(),
                    getBaseJavaClass().getName(),
                    instance.getClass().getName());
        }

        Object cdoId = getIdProperty().get(instance);
        if (cdoId == null) {
            throw new JaversException(JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID, getName(), getIdProperty().getName());
        }
        return cdoId;
    }

    public InstanceId createIdFromInstance(Object instance) {
        Object localId = getIdOf(instance);
        return new InstanceId(getName(), localId, localIdAsString(localId));
    }

    public InstanceId createIdFromInstanceId(Object localId) {
        return new InstanceId(getName(), localId, localIdAsString(localId));
    }

    private String localIdAsString(Object localId) {
        if (getIdPropertyType() instanceof EntityType) {
            EntityType idPropertyType = getIdPropertyType();
            return idPropertyType.localIdAsString(idPropertyType.getIdOf(localId));
        }

        PrimitiveOrValueType idPropertyType = getIdPropertyType();
        return idPropertyType.smartToString(localId);
    }

    private <T extends JaversType> T getIdPropertyType() {
        return getIdProperty().getType();
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

    @Override
    public String toString() {
        return ToStringBuilder.toString(this,
                "baseType", getBaseJavaType(),
                "id", getIdPropertyName());
    }

    @Override
    protected PrettyPrintBuilder prettyPrintBuilder() {
        return super.prettyPrintBuilder().addField("idProperty", getIdPropertyName());
    }
}
