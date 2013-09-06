package org.javers.model.mapping;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;

import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * immutable
 *
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class Entity<S> extends ManagedClass<S> {

    private final Property idProperty;

    public Entity(Class<S> sourceClass, List<Property> properties) {
        super(sourceClass, properties);
        argumentIsNotNull(sourceClass);
        argumentIsNotNull(properties);
        this.idProperty = findIdProperty();
    }

    private Property findIdProperty() {
        for (Property p : properties) {
            if (p.isId()) {
                return p;
            }
        }
        throw new JaversException(JaversExceptionCode.ENTITY_WITHOUT_ID,sourceClass.getName());
    }

    public Property getIdProperty() {
        return idProperty;
    }
}