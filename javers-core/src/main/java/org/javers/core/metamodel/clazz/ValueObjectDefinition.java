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
    private final boolean defaultType;

    /**
     * Simple recipe for ValueObject
     */
    public ValueObjectDefinition(Class<?> valueObject) {
        super(valueObject);
        this.defaultType = false;
    }

    /**
     * Recipe for ValueObject with ignoredProperties
     */
    public ValueObjectDefinition(Class<?> valueObject, List<String> ignoredProperties) {
        super(valueObject, ignoredProperties);
        this.defaultType = false;
    }

    ValueObjectDefinition(ValueObjectDefinitionBuilder builder) {
        super(builder);
        this.defaultType = builder.isDefault();

    }

    public boolean isDefault() {
        return defaultType;
    }
}
