package org.javers.model.mapping;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;

import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Class in client's domain model
 * <br/>
 *
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

    /**
     * @param cdo instance of {@link #getSourceClass()}
     * @return returns ID of given cdo
     */
    public Object getCdoIdOf(Object cdo) {
        Validate.argumentIsNotNull(cdo);
        Validate.argumentCheck(getSourceClass().isInstance(cdo),
                               "expected instance of "+getSourceClass().getName()+", got instance of "+cdo.getClass().getName());
        return getIdProperty().get(cdo);
    }

    public boolean isInstance(Object cdo) {
        return getSourceClass().isInstance(cdo);
    }

    public Property getIdProperty() {
        return idProperty;
    }
}