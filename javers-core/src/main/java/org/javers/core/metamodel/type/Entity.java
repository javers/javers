package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.metamodel.property.Property;

import java.util.List;

/**
 * @author Pawel Cierpiatka
 */
@Deprecated
class Entity extends ManagedClass {
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

    Property getIdProperty() {
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