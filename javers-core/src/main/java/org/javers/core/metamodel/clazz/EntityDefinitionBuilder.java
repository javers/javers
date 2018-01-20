package org.javers.core.metamodel.clazz;

import java.util.Optional;
import org.javers.common.validation.Validate;

/**
 * Fluent builder for {@link EntityDefinition},
 * allows to set all optional attributes:
 * Id-property, ignoredProperties and typeAlias, for example:
 * <pre>
 * EntityDefinitionBuilder.entityDefinition(Person.class)
 *    .withIdPropertyName(idPropertyName)
 *    .withIgnoredProperties(ignoredProperties)
 *    .withTypeName(typeName)
 *    .build();
 *</pre>
 *
 * @since 1.4
 * @author bartosz.walacik
 */
public class EntityDefinitionBuilder extends ClientsClassDefinitionBuilder<EntityDefinitionBuilder>{
    private Optional<String> idPropertyName = Optional.empty();
    private boolean shallowReference;

    EntityDefinitionBuilder(Class<?> entity) {
        super(entity);
    }

    public static EntityDefinitionBuilder entityDefinition(Class<?> entity) {
        return new EntityDefinitionBuilder(entity);
    }

    public EntityDefinitionBuilder withIdPropertyName(String idPropertyName) {
        Validate.argumentIsNotNull(idPropertyName);
        this.idPropertyName = Optional.of(idPropertyName);
        return this;
    }

    public EntityDefinitionBuilder withShallowReference(){
        this.shallowReference = true;
        return this;
    }

    @Override
    public EntityDefinition build() {
        return new EntityDefinition(this);
    }

    Optional<String> getIdPropertyName() {
        return idPropertyName;
    }

    boolean isShallowReference() {
        return shallowReference;
    }
}
