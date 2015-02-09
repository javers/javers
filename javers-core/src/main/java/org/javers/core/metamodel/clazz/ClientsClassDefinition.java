package org.javers.core.metamodel.clazz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Recipe for ClientsDomainClass
 *
 * @author bartosz walacik
 */
public abstract class ClientsClassDefinition {
    private final Class<?> clazz;
    private final List<String> ignoredProperties;


    ClientsClassDefinition(Class<?> clazz) {
        this(clazz, Collections.EMPTY_LIST);
    }

    ClientsClassDefinition(Class<?> clazz, List<String> ignoredProperties) {
        argumentsAreNotNull(clazz, ignoredProperties);
        this.clazz = clazz;
        this.ignoredProperties = new ArrayList<>(ignoredProperties);
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

    public List<String> getIgnoredProperties() {
        return Collections.unmodifiableList(ignoredProperties);
    }
}
