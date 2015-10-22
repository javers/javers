package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;

import java.util.List;

/**
 * @author Pawel Cierpiatka
 */
@Deprecated
public class Entity extends ManagedClass {
    private final Property idProperty;

    /**
     * @param idProperty null means - give me defaults
     */
    Entity(Class sourceClass, List<Property> properties, Property idProperty) {
        super(sourceClass, properties);

        if (idProperty == null) {
            this.idProperty = findDefaultIdProperty();
        }
        else {
            this.idProperty = idProperty;
        }
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

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Moved to EntityType
     *
     * @param instance instance of {@link #getClientsClass()}
     * @return returns ID of given instance so value of idProperty
     * @throws JaversException ENTITY_INSTANCE_WITH_NULL_ID
     * @throws JaversException NOT_INSTANCE_OF
     */
@Deprecated
    public Object getIdOf(Object instance) {
        Validate.argumentIsNotNull(instance);

        if (!getClientsClass().isInstance(instance)) {
            throw new JaversException(JaversExceptionCode.NOT_INSTANCE_OF, getName(), instance.getClass().getName());
        }
        Validate.argumentCheck(getClientsClass().isInstance(instance),
                               "expected instance of "+ getClientsClass().getName()+", got instance of "+ instance.getClass().getName());

        Object cdoId = getIdProperty().get(instance);
        if (cdoId == null) {
            throw new JaversException(JaversExceptionCode.ENTITY_INSTANCE_WITH_NULL_ID, getName());
        }
        return cdoId;
    }

    public Property getIdProperty() {
        return idProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || !(o instanceof Entity)) {return false;}

        Entity that = (Entity) o;
        return super.equals(that) && idProperty.equals(that.idProperty);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + idProperty.hashCode();
    }
}