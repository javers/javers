package org.javers.core.metamodel.clazz;

import java.util.List;
import org.javers.core.metamodel.type.ValueObjectType;

/**
 * Recipe for {@link ValueObjectType}
 *
 * @see ValueObjectDefinitionBuilder
 * @author bartosz walacik
 */
public class ValueObjectDefinition extends ClientsClassDefinition {

    /**
     * Simple recipe for ValueObject
     */
    public ValueObjectDefinition(Class<?> valueObject) {
        super(valueObject);
    }

    /**
     * Recipe for ValueObject with ignoredProperties
     */
    public ValueObjectDefinition(Class<?> valueObject, List<String> ignoredProperties) {
        super(valueObject, ignoredProperties);
    }

    ValueObjectDefinition(ClientsClassDefinitionBuilder builder) {
        super(builder);
    }
}
