package org.javers.core.metamodel.clazz;

import org.javers.core.metamodel.type.EntityType;
import java.util.List;
import static org.javers.common.collections.Lists.immutableCopyOf;

/**
 * Recipe for {@link EntityType}
 *
 * @see EntityDefinitionBuilder
 * @author bartosz walacik
 */
public class EntityDefinition  extends ClientsClassDefinition {
    private final List<String> idPropertyNames;
    private final boolean shallowReference;

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

    public EntityDefinition(Class<?> entity, String idPropertyName, List<String> ignoredProperties) {
        this(new EntityDefinitionBuilder(entity)
                .withIdPropertyName(idPropertyName)
                .withIgnoredProperties(ignoredProperties));
    }

    EntityDefinition(EntityDefinitionBuilder builder) {
        super(builder);
        this.idPropertyNames = immutableCopyOf(builder.getIdPropertyNames());
        this.shallowReference = builder.isShallowReference();
    }

    public boolean hasExplicitId() {
        return idPropertyNames.size() > 0;
    }

    /**
     * @return an immutable, non-null list
     */
    public List<String> getIdPropertyNames() {
        return idPropertyNames;
    }

    public boolean isShallowReference() {
        return shallowReference;
    }
}
