package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.type.EntityType;

import java.util.List;

/**
 * Recipe for {@link EntityType}
 *
 * @author bartosz walacik
 */
public class EntityDefinition  extends ClientsClassDefinition {
    private final Optional<String> idPropertyName;

    /**
     * Recipe for Entity with Id-property selected by @Id annotation
     */
    public EntityDefinition(Class<?> entity) {
        this(new EntityDefinitionBuilder(entity));
    }

    /**
     * Recipe for Entity with Id-property selected explicitly by name
     */
    public EntityDefinition(Class<?> entity, String idPropertyName){
        this(new EntityDefinitionBuilder(entity)
                .withIdPropertyName(idPropertyName));
    }

    private EntityDefinition(EntityDefinitionBuilder builder) {
        super(builder);
        this.idPropertyName = builder.idPropertyName;
    }

    /**
     * @deprecated use {@link EntityDefinitionBuilder}
     */
    @Deprecated
    public EntityDefinition(Class<?> entity, String idPropertyName, List<String> ignoredProperties) {
        this(new EntityDefinitionBuilder(entity)
                .withIdPropertyName(idPropertyName)
                .withIgnoredProperties(ignoredProperties));
    }

    public boolean hasCustomId() {
        return idPropertyName.isPresent();
    }

    public String getIdPropertyName() {
        return idPropertyName.get();
    }

    /**
     * Full recipe for Entity,
     * allows to set all optional attributes of EntityDefinition:
     * Id-property, ignoredProperties and typeAlias, for example:
     * <pre>
     * EntityDefinitionBuilder.entityDefinition(entity)
     *    .withIdPropertyName(idPropertyName)
     *    .withIgnoredProperties(ignoredProperties)
     *    .withTypeName(typeName)
     *    .build()
     *</pre>
     */
    public static class EntityDefinitionBuilder extends ClientsClassDefinitionBuilder<EntityDefinitionBuilder>{
        private Optional<String> idPropertyName = Optional.empty();

        private EntityDefinitionBuilder(Class<?> entity) {
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

        @Override
        public EntityDefinition build() {
            return new EntityDefinition(this);
        }
    }
}
