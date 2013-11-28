package org.javers.model.mapping;

import org.javers.common.validation.Validate;

/**
 * @author bartosz walacik
 */
public class EntityDefinition {
    private final Class<?> clazz;
    private final String idPropertyName;

    /**
     * gives you Entity with id property selected on the basis of @Id annotation
     */
    public EntityDefinition(Class<?> clazz) {
        Validate.argumentIsNotNull(clazz);
        this.clazz = clazz;
        this.idPropertyName = null;
    }

    /**
     * gives you Entity with id property selected explicitly by name
     */
    public EntityDefinition(Class<?> clazz, String idPropertyName) {
        Validate.argumentsAreNotNull(clazz, idPropertyName);
        this.clazz = clazz;
        this.idPropertyName = idPropertyName;
    }

    public boolean hasDefaultIdSelectionPolicy() {
        return idPropertyName == null;
    }

    public boolean hasCustomId() {
        return !hasDefaultIdSelectionPolicy();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getIdPropertyName() {
        return idPropertyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o || getClass() != o.getClass()) {
            return false;
        }

        EntityDefinition that = (EntityDefinition) o;

        return clazz.equals(that.clazz);
    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }
}
