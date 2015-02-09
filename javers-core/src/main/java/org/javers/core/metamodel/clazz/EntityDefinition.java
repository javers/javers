package org.javers.core.metamodel.clazz;

import java.util.Collections;
import java.util.List;
import org.javers.core.metamodel.type.EntityType;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Recipe for {@link EntityType}
 *
 * @author bartosz walacik
 */
public class EntityDefinition  extends ClientsClassDefinition {
    private final String idPropertyName;

    /**
     * Creates Entity with Id-property selected by @Id annotation
     */
    public EntityDefinition(Class<?> clazz) {
        super(clazz);
        this.idPropertyName = null;
    }

    /**
     * Creates Entity with Id-property selected explicitly by name
     */
    public EntityDefinition(Class<?> clazz, String idPropertyName){
        this(clazz, idPropertyName, Collections.<String>emptyList());
    }

    /**
     * Creates Entity with Id-property selected explicitly by name,
     * ignores given properties
     */
    public EntityDefinition(Class<?> clazz, String idPropertyName, List<String> ignoredProperties) {
        super(clazz,ignoredProperties);
        argumentIsNotNull(idPropertyName);
        this.idPropertyName = idPropertyName;
    }

    public boolean hasCustomId() {
        return idPropertyName != null;
    }

    public String getIdPropertyName() {
        return idPropertyName;
    }
}
