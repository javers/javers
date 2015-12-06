package org.javers.core.metamodel.clazz;

/**
 * Fluent builder for {@link ValueObjectDefinition},
 * allows to set all optional attributes:
 * ignoredProperties and typeName, for example:
 * <pre>
 * ValueObjectDefinitionBuilder.valueObjectDefinition(Address.class)
 *     .withIgnoredProperties(ignoredProperties)
 *     .withTypeName(typeName)
 *     .build();
 * </pre>
 *
 * @since 1.4
 * @author bartosz.walacik
 */
public class ValueObjectDefinitionBuilder extends ClientsClassDefinitionBuilder<ValueObjectDefinitionBuilder>{
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
