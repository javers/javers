package org.javers.core.metamodel.property;

import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.common.validation.Validate;

import java.util.List;

/**
 * Entity class in client's domain model.
 * Has list of mutable properties and its own identity hold in idProperty.
 * <br>
 *
 * Example:
 * <pre>
 *     class Person {
 *         private int    personId;
 *         private String firstName;
 *         private String lastName;
 *         ...
 *     }
 * </pre>
 * @author Pawel Cierpiatka
 */
public class Entity extends ManagedClass {
    private final Property idProperty;

    /**
     * @param idProperty null means - give me defaults
     */
    public Entity(Class sourceClass, List<Property> properties, Property idProperty) {
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

    /**
     * @param instance instance of {@link #getSourceClass()}
     * @return returns ID of given instance so unwrap of idProperty
     * @throws JaversException ENTITY_INSTANCE_WITH_NULL_ID
     * @throws JaversException NOT_INSTANCE_OF
     */
    public Object getIdOf(Object instance) {
        Validate.argumentIsNotNull(instance);

        if (!getSourceClass().isInstance(instance)) {
            throw new JaversException(JaversExceptionCode.NOT_INSTANCE_OF, getName(), instance.getClass().getName());
        }
        Validate.argumentCheck(getSourceClass().isInstance(instance),
                               "expected instance of "+getSourceClass().getName()+", got instance of "+ instance.getClass().getName());

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