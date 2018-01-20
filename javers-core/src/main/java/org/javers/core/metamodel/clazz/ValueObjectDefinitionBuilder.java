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
public class ValueObjectDefinitionBuilder extends ClientsClassDefinitionBuilder<ValueObjectDefinitionBuilder> {
    private boolean defaultType;

    private ValueObjectDefinitionBuilder(Class valueObject) {
        super(valueObject);
    }

    public static ValueObjectDefinitionBuilder valueObjectDefinition(Class<?> valueObject) {
        return new ValueObjectDefinitionBuilder(valueObject);
    }

    public ValueObjectDefinitionBuilder defaultType() {
        this.defaultType = true;
        return this;
    }

    @Override
    public ValueObjectDefinition build() {
        return new ValueObjectDefinition(this);
    }

    boolean isDefault() {
        return defaultType;
    }
}
