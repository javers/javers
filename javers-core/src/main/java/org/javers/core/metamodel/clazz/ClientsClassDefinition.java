package org.javers.core.metamodel.clazz;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Recipe for ClientsDomainClass
 *
 * @author bartosz walacik
 */
public abstract class ClientsClassDefinition {
    private final Class<?> clazz;

    ClientsClassDefinition(Class<?> clazz) {
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

        ClientsClassDefinition that = (ClientsClassDefinition) o;

        return clazz.equals(that.clazz);
    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }
}
