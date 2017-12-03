package org.javers.core.metamodel.clazz;

import org.javers.common.collections.Lists;
import java.util.Optional;
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
     * See {@link ClientsClassDefinition#getIgnoredProperties()}
     */
    public T withIgnoredProperties(String... ignoredProperties) {
        withIgnoredProperties(Lists.asList(ignoredProperties));
        return (T) this;
    }

    /**
     * See {@link ClientsClassDefinition#getIgnoredProperties()}
     */
    public T withIgnoredProperties(List<String> ignoredProperties) {
        Validate.argumentIsNotNull(ignoredProperties);
        this.ignoredProperties = ignoredProperties;
        return (T) this;
    }

    /**
     * See {@link ClientsClassDefinition#getIncludedProperties()}
     */
    public T withIncludedProperties(List<String> includedProperties) {
        Validate.argumentIsNotNull(includedProperties);
        this.ignoredProperties = includedProperties;
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

    public Class<?> getClazz() {
        return clazz;
    }

    public List<String> getIgnoredProperties() {
        return ignoredProperties;
    }

    public List<String> getIncludedProperties() {
        return includedProperties;
    }

    public Optional<String> getTypeName() {
        return typeName;
    }

}
