package org.javers.model.mapping;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class ValueObject<S> extends ManagedClass<S> {
    public ValueObject(Class<S> sourceClass, List<Property> properties) {
        super(sourceClass, properties);
    }
}
