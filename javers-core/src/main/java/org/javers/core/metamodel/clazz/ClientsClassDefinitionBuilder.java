package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Lists;
import java.util.Optional;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;

import java.util.Collections;
import java.util.List;

/**
 * @since 1.4
 * @author bartosz.walacik
 */
public abstract class ClientsClassDefinitionBuilder<T extends ClientsClassDefinitionBuilder> {
    private Class<?> clazz;
    private List<String> ignoredProperties = Collections.emptyList();
    private List<String> includedProperties = Collections.emptyList();
    private Optional<String> typeName = Optional.empty();

    ClientsClassDefinitionBuilder(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * See {@link PropertiesFilter#getIgnoredProperties()}
     * @throws IllegalArgumentException If includedProperties was already set. You can either specify includedProperties or ignoredProperties, not both.
     */
    public T withIgnoredProperties(String... ignoredProperties) {
        withIgnoredProperties(Lists.asList(ignoredProperties));
        return (T) this;
    }

    /**
     * See {@link PropertiesFilter#getIgnoredProperties()}
     * @throws JaversException If includedProperties was already set. You can either specify includedProperties or ignoredProperties, not both.
     */
    public T withIgnoredProperties(List<String> ignoredProperties) {
        Validate.argumentIsNotNull(ignoredProperties);
        if (includedProperties.size() > 0) {
            throw new JaversException(JaversExceptionCode.IGNORED_AND_INCLUDED_PROPERTIES_MIX, clazz.getSimpleName());
        }
        this.ignoredProperties = ignoredProperties;
        return (T) this;
    }

    /**
     * See {@link PropertiesFilter#getIncludedProperties()}
     * @throws JaversException If ignoredProperties was already set. You can either specify includedProperties or ignoredProperties, not both.
     */
    public T withIncludedProperties(List<String> includedProperties) {
        Validate.argumentIsNotNull(includedProperties);
        if (ignoredProperties.size() > 0) {
            throw new JaversException(JaversExceptionCode.IGNORED_AND_INCLUDED_PROPERTIES_MIX, clazz.getSimpleName());
        }
        this.includedProperties = includedProperties;
        return (T) this;
    }

    public T withTypeName(Optional<String> typeName) {
        Validate.argumentIsNotNull(typeName);
        this.typeName = typeName;
        return (T) this;
    }

    public T withTypeName(String typeName) {
        return withTypeName(Optional.ofNullable(typeName));
    }

    public ClientsClassDefinition build() {
        throw new RuntimeException("not implemented");
    }

    Class<?> getClazz() {
        return clazz;
    }

    List<String> getIgnoredProperties() {
        return ignoredProperties;
    }

    List<String> getIncludedProperties() {
        return includedProperties;
    }

    Optional<String> getTypeName() {
        return typeName;
    }

}
