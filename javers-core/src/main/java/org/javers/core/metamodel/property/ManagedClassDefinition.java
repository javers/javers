package org.javers.core.metamodel.property;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public abstract class ManagedClassDefinition {
    private final Class<?> clazz;

    ManagedClassDefinition(Class<?> clazz) {
        argumentIsNotNull(clazz);
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o || getClass() != o.getClass()) {
            return false;
        }

        ManagedClassDefinition that = (ManagedClassDefinition) o;

        return clazz.equals(that.clazz);
    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }
}
