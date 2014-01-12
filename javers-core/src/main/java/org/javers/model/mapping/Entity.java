package org.javers.model.mapping;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Entity class in client's domain model.
 * Has list of mutable properties and its own identity hold in idProperty.
 * <p/>
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
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class Entity extends ValueObject {
    private static final Logger logger = LoggerFactory.getLogger(Entity.class);
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

    private Property findDefaultIdProperty() {
        for (Property p : getProperties()) {
            if (p.looksLikeId()) {
                return p;
            }
        }
        throw new JaversException(JaversExceptionCode.ENTITY_WITHOUT_ID,sourceClass.getName());
    }

    /**
     * @param instance instance of {@link #getSourceClass()}
     * @return returns ID of given instance so value of idProperty
     */
    public Object getIdOf(Object instance) {
        Validate.argumentIsNotNull(instance);
        Validate.argumentCheck(getSourceClass().isInstance(instance),
                               "expected instance of "+getSourceClass().getName()+", got instance of "+ instance.getClass().getName());
        return getIdProperty().get(instance);
    }

    public boolean isInstance(Object cdo) {
        return getSourceClass().isInstance(cdo);
    }

    public Property getIdProperty() {
        return idProperty;
    }

}