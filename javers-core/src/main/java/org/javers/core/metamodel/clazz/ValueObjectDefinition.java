package org.javers.core.metamodel.clazz;

import java.util.List;
import org.javers.core.metamodel.type.ValueObjectType;

/**
 * Recipe for {@link ValueObjectType}
 *
 * @author bartosz walacik
 */
public class ValueObjectDefinition extends ClientsClassDefinition {

    public ValueObjectDefinition(Class<?> valueObject) {
        super(valueObject);
    }

    public ValueObjectDefinition(Class<?> valueObject, List<String> ignoredProperties) {
        super(valueObject, ignoredProperties);
    }

    private ValueObjectDefinition(ClientsClassDefinitionBuilder builder) {
        super(builder);
    }

    /**
     * Full recipe for ValueObject,
     * allows to set all optional attributes of ValueObjectDefinition:
     * ignoredProperties and typeName, for example:
     * <pre>
     * ValueObjectDefinitionBuilder.valueObjectDefinition(valueObject)
     *     .withIgnoredProperties(ignoredProperties)
     *     .withTypeName(typeName)
     *     .build()
     * </pre>
     */
    public static class ValueObjectDefinitionBuilder extends ClientsClassDefinitionBuilder<ValueObjectDefinitionBuilder>{
        private ValueObjectDefinitionBuilder(Class valueObject) {
            super(valueObject);
        }

        public static ValueObjectDefinitionBuilder valueObjectDefinition(Class<?> valueObject) {
            return new ValueObjectDefinitionBuilder(valueObject);
        }

        @Override
        public ValueObjectDefinition build() {
            return new ValueObjectDefinition(this);
        }
    }
}
