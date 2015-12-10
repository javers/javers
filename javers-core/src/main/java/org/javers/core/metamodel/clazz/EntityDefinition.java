package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Optional;
import org.javers.core.metamodel.type.EntityType;

import java.util.List;

/**
 * Recipe for {@link EntityType}
 *
 * @see EntityDefinitionBuilder
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

    EntityDefinition(EntityDefinitionBuilder builder) {
        super(builder);
        this.idPropertyName = builder.getIdPropertyName();
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

}
