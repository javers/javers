package org.javers.core.metamodel.clazz;

import java.util.List;
import org.javers.core.metamodel.type.ValueObjectType;

/**
 * Recipe for {@link ValueObjectType}
 *
 * @author bartosz walacik
 */
public class ValueObjectDefinition extends ClientsClassDefinition {

    /**
     * Basic definition, all properties of given class will be versioned
     */
    public ValueObjectDefinition(Class<?> valueObjectClass) {
        super(valueObjectClass);
    }

    /**
     * Creates ValueObject, ignores given properties
     */
    public ValueObjectDefinition(Class<?> valueObjectClass, List<String> ignoredProperties) {
        super(valueObjectClass, ignoredProperties);
    }
}
