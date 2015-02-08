package org.javers.core.metamodel.clazz;

import java.util.Collections;
import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public class EntityDefinition  extends ClientsClassDefinition {
    private final String idPropertyName;

    /**
     * creates Entity with id property selected by @Id annotation
     */
    public EntityDefinition(Class<?> clazz) {
        super(clazz);
        this.idPropertyName = null;
    }

    /**
     * creates Entity with id property selected explicitly by name
     */
    public EntityDefinition(Class<?> clazz, String idPropertyName){
        this(clazz, idPropertyName, Collections.<String>emptyList());
    }

    /**
     * creates Entity with id property selected explicitly by name,
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
